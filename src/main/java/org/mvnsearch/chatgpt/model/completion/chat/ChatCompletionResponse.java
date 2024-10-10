package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.mvnsearch.chatgpt.model.CompletionUsage;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionResponse {

	private String id;

	private String model;

	private Long created;

	private String object;

	private CompletionUsage usage;

	private List<ChatCompletionChoice> choices;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CompletionUsage getUsage() {
		return usage;
	}

	public void setUsage(CompletionUsage usage) {
		this.usage = usage;
	}

	public List<ChatCompletionChoice> getChoices() {
		return choices;
	}

	public void setChoices(List<ChatCompletionChoice> choices) {
		this.choices = choices;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return choices == null || choices.isEmpty();
	}

	@JsonIgnore
	public List<ChatMessage> getReply() {
		if (isEmpty())
			return Collections.emptyList();
		return this.choices.stream().map(ChatCompletionChoice::getMessage).toList();
	}

	/**
	 * get reply text from messages' content
	 * @return reply text
	 */
	@JsonIgnore
	public String getReplyText() {
		if (isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		for (ChatCompletionChoice choice : choices) {
			final ChatMessage message = choice.getMessage();
			if (message != null && message.getContent() != null) {
				sb.append(message.getContent());
			}
		}
		return sb.toString();
	}

	@JsonIgnore
	public <T> T getStructuredOutput(Class<T> clazz) throws Exception {
		if (isEmpty())
			return null;
		String jsonContent = "";
		String refusal = null;
		for (ChatCompletionChoice choice : choices) {
			final ChatMessage message = choice.getMessage();
			if (message != null) {
				if (message.getContent() != null) {
					jsonContent = message.getContent();
					break;
				}
				if (message.getRefusal() != null) {
					refusal = message.getRefusal();
				}
			}

		}
		if (!jsonContent.startsWith("{")) {
			throw new Exception("No structured output found: " + refusal);
		}
		return GPTFunctionUtils.objectMapper.readValue(jsonContent, clazz);
	}

	@JsonIgnore
	public Mono<String> getReplyCombinedText() {
		if (isEmpty())
			return Mono.empty();
		return Flux.fromIterable(choices).flatMap(choice -> {
			final ChatMessage message = choice.getMessage();
			if (message != null) {
				return message.getReplyCombinedText();
			}
			return Mono.empty();
		}).collectList().map(items -> items.stream().filter(Objects::nonNull).collect(Collectors.joining()));
	}

	@JsonIgnore
	public <T> Mono<T> getFunctionResult() {
		if (isEmpty())
			return Mono.empty();
		return Flux.fromIterable(choices)
			.filter(choice -> choice.getMessage() != null && choice.getMessage().getToolCalls() != null)
			.last()
			.flatMap(choice -> choice.getMessage().getFunctionResult());
	}

}
