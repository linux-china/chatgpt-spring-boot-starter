package org.mvnsearch.chatgpt.model;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;

public class ChatCompletionRequestTest {

    @Test
    public void testToJson() throws Exception {
        final ChatCompletionRequest request = ChatCompletionRequest.of("What's Java Language?");
        request.setFunctionCall("hello_java");
        System.out.println(GPTFunctionUtils.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
    }
}
