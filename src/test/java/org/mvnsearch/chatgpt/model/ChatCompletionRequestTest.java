package org.mvnsearch.chatgpt.model;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatCompletionRequestTest {

    @Test
    public void testToJson() throws Exception {
        final ChatCompletionRequest request = ChatCompletionRequest.of("What's Java Language?");
        request.setFunctionCall("hello_java");
        final String expected = """
                {
                  "model" : "gpt-3.5-turbo",
                  "messages" : [ {
                    "role" : "user",
                    "content" : "What's Java Language?"
                  } ],
                  "function_call" : {
                    "name" : "hello_java"
                  }
                }""";
        final String actual = GPTFunctionUtils.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);

        assertEquals(expected, actual);
    }
}
