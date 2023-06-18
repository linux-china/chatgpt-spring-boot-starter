package org.mvnsearch.chatgpt.model.function;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.service.GPTFunctions;

import java.util.Map;

public class GPTFunctionUtilsTest {

    @Test
    public void testExtractFunctions() throws Exception {
        final var functions = GPTFunctionUtils.extractFunctions(GPTFunctions.class);
        for (Map.Entry<String, ChatGPTJavaFunction> entry : functions.entrySet()) {
            final ChatGPTJavaFunction javaFuntion = entry.getValue();
            System.out.println(javaFuntion.getJavaMethod().getName());
        }
    }
}
