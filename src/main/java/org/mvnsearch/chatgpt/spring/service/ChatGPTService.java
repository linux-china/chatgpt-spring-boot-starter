package org.mvnsearch.chatgpt.spring.service;

import org.jetbrains.annotations.PropertyKey;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static org.mvnsearch.chatgpt.spring.service.PromptManager.PROMPTS_FQN;

public interface ChatGPTService {

	Mono<ChatCompletionResponse> chat(ChatCompletionRequest request);

	Flux<ChatCompletionResponse> stream(ChatCompletionRequest request);

	<T> Function<T, Mono<String>> promptAsLambda(@PropertyKey(resourceBundle = PROMPTS_FQN) String promptKey);

	<T, R> Function<T, Mono<R>> promptAsLambda(@PropertyKey(resourceBundle = PROMPTS_FQN) String promptKey,
			String functionName);

}
