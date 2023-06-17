package org.mvnsearch.chatgpt.spring.http;

import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@HttpExchange("https://api.openai.com")
public interface OpenAIChatAPI {
    @PostExchange("/v1/chat/completions")
    Mono<ChatCompletionResponse> chat(@RequestBody ChatCompletionRequest request);

    @PostExchange("/v1/chat/completions")
    Flux<ChatCompletionResponse> stream(@RequestBody ChatCompletionRequest request);

}
