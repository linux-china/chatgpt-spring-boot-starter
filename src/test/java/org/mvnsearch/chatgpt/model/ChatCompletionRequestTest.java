package org.mvnsearch.chatgpt.model;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatCompletionRequestTest {

	@Test
	void testToJson() throws Exception {
		ChatCompletionRequest request = ChatCompletionRequest.of("What's Java Language?");
		request.setFunctionCall("hello_java");
		String expected = """
				{
				  "messages" : [ {
				    "role" : "user",
				    "content" : "What's Java Language?"
				  } ],
				  "function_call" : {
				    "name" : "hello_java"
				  }
				}""";
		String actual = GPTFunctionUtils.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

		System.out.println(expected);
		System.out.println(actual);
		assertEquals(expected, actual);
	}

}
