package org.mvnsearch.chatgpt.demo.service;

import org.mvnsearch.chatgpt.model.function.Parameter;
import org.mvnsearch.chatgpt.model.output.StructuredOutput;

import java.util.List;

/**
 * Java example for structured output
 * 
 * @author linux_china
 */
@StructuredOutput(name = "java_example")
public record JavaExample(@Parameter("explanation") String explanation, @Parameter("answer") String answer,
		@Parameter("code") String code, @Parameter("dependencies") List<String> dependencies) {
}
