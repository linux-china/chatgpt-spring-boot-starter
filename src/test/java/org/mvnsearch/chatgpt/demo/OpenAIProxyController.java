package org.mvnsearch.chatgpt.demo;

import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionResponse;
import org.mvnsearch.chatgpt.spring.service.OpenAIChatAPI;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OpenAIProxyController {

	@Autowired
	private OpenAIChatAPI openAIChatAPI;

	@PostMapping("/v1/chat/completions")
	Publisher<ChatCompletionResponse> completions(@RequestBody ChatCompletionRequest request) {
		return openAIChatAPI.proxy(request);
	}

}