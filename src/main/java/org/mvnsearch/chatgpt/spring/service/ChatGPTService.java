package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatGPTService {
    Mono<ChatCompletionResponse> chat(ChatCompletionRequest request);

    Flux<ChatCompletionResponse> stream(ChatCompletionRequest request);

}
