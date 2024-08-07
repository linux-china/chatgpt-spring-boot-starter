package org.mvnsearch.chatgpt.demo.service;

import jakarta.annotation.Nonnull;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.mvnsearch.chatgpt.model.output.StructuredOutput;

import java.util.List;

/**
 * Java example for structured output
 * 
 * @author linux_china
 */
@StructuredOutput(name = "java_example")
public record JavaExample(@Nonnull @Parameter("explanation") String explanation,
		@Nonnull @Parameter("answer") String answer, @Nonnull @Parameter("code") String code,
		@Nonnull @Parameter("dependencies") List<String> dependencies) {
}
