package org.mvnsearch.chatgpt.spring.service;

import org.jetbrains.annotations.PropertyKey;
import org.mvnsearch.chatgpt.model.completion.chat.*;
import org.mvnsearch.chatgpt.model.completion.completion.CompletionRequest;
import org.mvnsearch.chatgpt.model.completion.completion.CompletionResponse;
import org.mvnsearch.chatgpt.model.embedding.EmbeddingsRequest;
import org.mvnsearch.chatgpt.model.embedding.EmbeddingsResponse;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.spring.constants.ChatGPTConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.mvnsearch.chatgpt.spring.service.PromptManager.PROMPTS_FQN;

class ChatGPTServiceImpl implements ChatGPTService {

	private final OpenAIChatAPI openAIChatAPI;

	private final PromptManager promptManager;

	private final GPTFunctionRegistry registry;

	private String model = ChatGPTConstants.DEFAULT_MODEL;

	ChatGPTServiceImpl(OpenAIChatAPI openAIChatAPI, PromptManager promptManager, GPTFunctionRegistry registry)
			throws Exception {
		this.openAIChatAPI = openAIChatAPI;
		this.promptManager = promptManager;
		this.registry = registry;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Override
	public Mono<ChatCompletionResponse> chat(ChatCompletionRequest request) {
		buildChatCompletionRequest(request);
		request.setStream(null);
		boolean functionsIncluded = request.getTools() != null;
		if (!functionsIncluded) {
			return this.openAIChatAPI//
				.chat(request);
		} //
		else {
			return this.openAIChatAPI.chat(request)//
				.doOnNext(response -> {
					for (ChatMessage chatMessage : response.getReply()) {
						injectFunctionCallLambda(chatMessage);
					}
				});
		}
	}

	@Override
	public Flux<ChatCompletionResponse> stream(ChatCompletionRequest request) {
		buildChatCompletionRequest(request);
		request.setStream(true);
		boolean functionsIncluded = request.getTools() != null;
		if (!functionsIncluded) {
			return openAIChatAPI.stream(request).onErrorContinue((e, obj) -> {
			});
		}
		else {
			return openAIChatAPI.stream(request).onErrorContinue((e, obj) -> {
			}).doOnNext(response -> {
				for (ChatMessage chatMessage : response.getReply()) {
					injectFunctionCallLambda(chatMessage);
				}
			});
		}
	}

	@Override
	public Mono<EmbeddingsResponse> embed(EmbeddingsRequest request) {
		return this.openAIChatAPI.embed(request);
	}

	@Override
	public Mono<CompletionResponse> complete(CompletionRequest request) {
		return this.openAIChatAPI.complete(request);
	}

	@Override
	public <T> Function<T, Mono<String>> promptAsLambda(@PropertyKey(resourceBundle = PROMPTS_FQN) String promptKey) {
		return promptAsLambda(promptKey, null);
	}

	@Override
	public <T, R> Function<T, Mono<R>> promptAsLambda(@PropertyKey(resourceBundle = PROMPTS_FQN) String promptKey,
			String functionName) {
		return obj -> {
			String prompt = promptManager.prompt(promptKey, obj);
			final ChatCompletionRequest request = ChatRequestBuilder.of(prompt).model(model).build();
			if (functionName != null && !functionName.isEmpty()) {
				request.addFunction(functionName);
				return (Mono<R>) chat(request).flatMap(ChatCompletionResponse::getFunctionResult);
			}
			else {
				return (Mono<R>) chat(request).map(ChatCompletionResponse::getReplyText);
			}
		};
	}

	private void buildChatCompletionRequest(ChatCompletionRequest request) {
		if (request.getModel() == null) {
			request.setModel(model);
		}
		injectFunctions(request);
	}

	private void injectFunctions(ChatCompletionRequest request) {
		final List<String> functionNames = request.getFunctionNames();
		if (functionNames != null && !functionNames.isEmpty()) {
			for (String functionName : functionNames) {
				ChatFunction chatFunction = this.registry.getChatFunction(functionName);
				if (chatFunction != null) {
					request.addFunction(chatFunction);
				}
			}
		}
	}

	private void injectFunctionCallLambda(ChatMessage chatMessage) {
		final FunctionCall functionCall = chatMessage.findFunctionCall();
		if (functionCall != null) {
			final String functionName = functionCall.getName();
			ChatGPTJavaFunction jsonSchemaFunction = registry.getJsonSchemaFunction(functionName);
			if (jsonSchemaFunction != null) {
				functionCall.setFunctionStub(() -> jsonSchemaFunction.call(functionCall.getArguments()));
			}
		}
	}

}
