package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatCompletionBatchRequestTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testGenerateJsonl() throws Exception {
		String jsonl = Stream.of("What's Java Language?", "What's Kotlin Language?")
			.map(ChatCompletionRequest::of)
			.map(ChatCompletionBatchRequest::new)
			.map(this::toJson)
			.filter(Strings::isNotBlank)
			.collect(Collectors.joining("\n"));
		System.out.println(jsonl);
	}

	@Test
	public void testResponseParse() throws Exception {
		final InputStream inputStream = this.getClass().getResourceAsStream("/batch-responses.jsonl");
		assert inputStream != null;
		List<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines().toList();
		for (String line : lines) {
			if (line.startsWith("{")) {
				ChatCompletionBatchResponse response = objectMapper.readValue(line, ChatCompletionBatchResponse.class);
				System.out.println(response.getCustomId());
			}
		}
	}

	private String toJson(ChatCompletionBatchRequest request) {
		try {
			return objectMapper.writeValueAsString(request);
		}
		catch (Exception e) {
			return "";
		}
	}

}
