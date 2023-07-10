package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionResponse;
import org.mvnsearch.chatgpt.model.completion.completion.CompletionRequest;
import org.mvnsearch.chatgpt.model.completion.completion.CompletionResponse;
import org.mvnsearch.chatgpt.model.embedding.EmbeddingsRequest;
import org.mvnsearch.chatgpt.model.embedding.EmbeddingsResponse;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OpenAIChatAPI {

	@PostExchange("/chat/completions")
	Mono<ChatCompletionResponse> chat(@RequestBody ChatCompletionRequest request);

	@PostExchange("/chat/completions")
	Flux<ChatCompletionResponse> stream(@RequestBody ChatCompletionRequest request);

	/**
	 * proxy to upstream
	 * @param request chat completion request
	 * @return response
	 */
	@PostExchange("/chat/completions")
	Publisher<ChatCompletionResponse> proxy(@RequestBody ChatCompletionRequest request);

	@PostExchange("/embeddings")
	Mono<EmbeddingsResponse> embed(@RequestBody EmbeddingsRequest request);

	@PostExchange("/completions")
	Mono<CompletionResponse> complete(@RequestBody CompletionRequest request);

}
