package org.mvnsearch.chatgpt.model.function;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.service.GPTFunctions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class GPTFunctionUtilsTest {

    @Test
    public void testExtractFunctions() throws Exception {

        final var expected = List.of("sendEmail", "executeSQLQuery");
        final var actual = GPTFunctionUtils.extractFunctions(GPTFunctions.class)
                .values()
                .stream()
                .map(m -> m.getJavaMethod().getName())
                .toList();

        assertIterableEquals(expected, actual);
    }
}
