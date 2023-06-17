package org.mvnsearch.chatgpt.model.function;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.service.GPTFunctions;

public class GPTFunctionUtilsTest {

    @Test
    public void testExtractFunctions() throws Exception {
        final var functions = GPTFunctionUtils.extractFunctions(GPTFunctions.class);
        System.out.println(GPTFunctionUtils.toFunctionsJsonArray(functions.values()));
    }
}
