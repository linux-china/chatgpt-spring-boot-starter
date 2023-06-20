package org.mvnsearch.chatgpt.demo;

import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.spring.http.OpenAIChatAPI;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAIProxyController {
    @Autowired
    private OpenAIChatAPI openAIChatAPI;

    /**
     * Chat proxy to OpenAI API
     *
     * @param request chat request
     * @return response
     */
    @PostMapping("/v1/chat/completions")
    public Publisher<ChatCompletionResponse> completions(@RequestBody ChatCompletionRequest request) {
        return openAIChatAPI.proxy(request);
    }
}