package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class OpenAIChatAPITest extends ProjectBootBaseTest {

	@Autowired
	private OpenAIChatAPI openAIChatAPI;

	// @Test
	// todo why is this broken?
	void call() {

		ChatCompletionRequest request = ChatCompletionRequest
			.of("What is the history of the Java programming Language? Please give me simple example, and guide me how to run the example.");
		Mono<String> chat = openAIChatAPI.chat(request).flatMap(ChatCompletionResponse::getReplyCombinedText);
		StepVerifier.create(chat)//
			.expectNextMatches(s -> s.contains("Sun Microsystems"))//
			.verifyComplete();
	}

}