package org.mvnsearch.chatgpt.spring.http;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenAIChatAPITest extends ProjectBootBaseTest {

	@Autowired
	private OpenAIChatAPI openAIChatAPI;

	@Test
	public void testStream() throws Exception {
		ChatCompletionRequest request = ChatCompletionRequest
			.of("What's Java Language? Please give me simple example, and guide me how to run the example.");
		request.setStream(true);
		openAIChatAPI.stream(request).subscribe(response -> {
			System.out.println(response.getReplyText());
		});
		Thread.sleep(60000);
	}

}
