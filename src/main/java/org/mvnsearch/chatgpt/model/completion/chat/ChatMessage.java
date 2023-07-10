package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import reactor.core.publisher.Mono;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {

	private ChatMessageRole role;

	private String content;

	/**
	 * the name of the author of this message
	 */
	private String name;

	@JsonProperty("function_call")
	private FunctionCall functionCall;

	public ChatMessage() {
	}

	public ChatMessage(ChatMessageRole role, String content) {
		this.role = role;
		this.content = content;
	}

	public ChatMessage(ChatMessageRole role, String content, String name, FunctionCall functionCall) {
		this.role = role;
		this.content = content;
		this.name = name;
		this.functionCall = functionCall;
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

	public FunctionCall getFunctionCall() {
		return functionCall;
	}

	public void setFunctionCall(FunctionCall functionCall) {
		this.functionCall = functionCall;
	}

	@JsonIgnore
	public Mono<String> getReplyCombinedText() {
		if (content != null) {
			return Mono.just(content);
		}
		if (functionCall != null && functionCall.getFunctionStub() != null) {
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
		return Mono.empty();
	}

	@JsonIgnore
	public <T> Mono<T> getFunctionResult() {
		if (functionCall != null && functionCall.getFunctionStub() != null) {
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
