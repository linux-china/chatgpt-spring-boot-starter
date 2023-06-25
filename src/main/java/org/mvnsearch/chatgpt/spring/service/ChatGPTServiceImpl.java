package org.mvnsearch.chatgpt.spring.service;

import org.jetbrains.annotations.PropertyKey;
import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import org.mvnsearch.chatgpt.spring.constants.ChatGPTConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.RecordComponent;
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
		boolean functionsIncluded = request.getFunctions() != null;
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
		boolean functionsIncluded = request.getFunctions() != null;
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
	public <T> Function<T, Mono<String>> promptAsLambda(@PropertyKey(resourceBundle = PROMPTS_FQN) String promptKey) {
		return promptAsLambda(promptKey, null);
	}

	@Override
	public <T, R> Function<T, Mono<R>> promptAsLambda(@PropertyKey(resourceBundle = PROMPTS_FQN) String promptKey,
			String functionName) {
		return obj -> {
			String prompt;
			if (obj != null) {
				if (obj.getClass().isArray()) { // array
					Object[] args = (Object[]) obj;
					prompt = promptManager.prompt(promptKey, args);
				}
				else if (obj instanceof List<?> list) { // list
					prompt = promptManager.prompt(promptKey, list.toArray());
				}
				else if (obj.getClass().isRecord()) { // record
					prompt = promptManager.prompt(promptKey, GPTFunctionUtils.convertRecordToArray(obj));
				}
				else { // object
					prompt = promptManager.prompt(promptKey, obj);
				}
			}
			else {
				prompt = promptManager.prompt(promptKey);
			}
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

	private void buildChatCompletionRequest(ChatCompletionRequest request){
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
		request.updateModelWithFunctionSupport();
	}

	private void injectFunctionCallLambda(ChatMessage chatMessage) {
		final FunctionCall functionCall = chatMessage.getFunctionCall();
		if (functionCall != null) {
			final String functionName = functionCall.getName();
			ChatGPTJavaFunction jsonSchemaFunction = registry.getJsonSchemaFunction(functionName);
			if (jsonSchemaFunction != null) {
				functionCall.setFunctionStub(() -> jsonSchemaFunction.call(functionCall.getArguments()));
			}
		}
	}

}
