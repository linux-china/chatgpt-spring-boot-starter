package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {

	private ChatMessageRole role;

	private String content;

	/**
	 * the name of the author of this message
	 */
	private String name;

	@JsonProperty("tool_calls")
	private List<ToolCall> toolCalls;

	public ChatMessage() {
	}

	public ChatMessage(ChatMessageRole role, String content) {
		this.role = role;
		this.content = content;
	}

	public ChatMessageRole getRole() {
		return role;
	}

	public void setRole(ChatMessageRole role) {
		this.role = role;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ToolCall> getToolCalls() {
		return toolCalls;
	}

	public void setToolCalls(List<ToolCall> toolCalls) {
		this.toolCalls = toolCalls;
	}

	public FunctionCall findFunctionCall() {
		if (toolCalls != null && !toolCalls.isEmpty()) {
			for (ToolCall toolCall : toolCalls) {
				if (Objects.equals(toolCall.getType(), "function")) {
					return toolCall.getFunction();
				}
			}
		}
		return null;
	}

	@JsonIgnore
	public Mono<String> getReplyCombinedText() {
		if (content != null) {
			return Mono.just(content);
		}
		if (toolCalls != null) {
			for (ToolCall toolCall : toolCalls) {
				final FunctionCall functionCall = toolCall.getFunction();
				if (functionCall.getFunctionStub() != null) {
					try {
						final Object result = functionCall.getFunctionStub().call();
						if (result != null) {
							if (result instanceof Mono) {
								return ((Mono<?>) result).map(GPTFunctionUtils::toTextPlain);
							}
							else {
								return Mono.just(result).map(GPTFunctionUtils::toTextPlain);
							}
						}
					}
					catch (Exception e) {
						return Mono.error(e);
					}
				}
			}

		}
		return Mono.empty();
	}

	@JsonIgnore
	public <T> Mono<T> getFunctionResult() {
		if (toolCalls != null) {
			final FunctionCall functionCall = toolCalls.get(0).getFunction();
			if (functionCall.getFunctionStub() != null) {
				try {
					final Object result = functionCall.getFunctionStub().call();
					if (result != null) {
						if (result instanceof Mono) {
							return (Mono<T>) result;
						}
						else {
							return (Mono<T>) Mono.just(result);
						}
					}
				}
				catch (Exception e) {
					return Mono.error(e);
				}
			}
		}
		return Mono.empty();
	}

	public static ChatMessage systemMessage(@Nonnull String content) {
		return new ChatMessage(ChatMessageRole.system, content);
	}

	public static ChatMessage userMessage(@Nonnull String content) {
		return new ChatMessage(ChatMessageRole.user, content);
	}

	public static ChatMessage assistantMessage(@Nonnull String content) {
		return new ChatMessage(ChatMessageRole.assistant, content);
	}

}
