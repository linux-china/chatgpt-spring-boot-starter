package org.mvnsearch.chatgpt.model;

import org.jetbrains.annotations.PropertyKey;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

import static org.mvnsearch.chatgpt.spring.service.PromptManager.PROMPTS_FQN;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChatCompletion {

	/**
	 * user prompt, and placeholder is {0}, {1}, {2} fo method parameters
	 * @return system prompt
	 */
	String value() default "";

	/**
	 * system prompt
	 * @return system prompt
	 */
	String system() default "";

	/**
	 * user prompt, and placeholder is {0}, {1}, {2} fo method parameters
	 * @return user prompt
	 */
	@AliasFor("value")
	String user() default "";

	/**
	 * assistant prompt
	 * @return assistant prompt
	 */
	String assistant() default "";

	/**
	 * user prompt template from prompts.properties
	 * @return user prompt template
	 */
	@PropertyKey(resourceBundle = PROMPTS_FQN)
	String userTemplate() default "";

	/**
	 * system prompt template from prompts.properties
	 * @return system prompt template
	 */
	@PropertyKey(resourceBundle = PROMPTS_FQN)
	String systemTemplate() default "";

	/**
	 * assistant prompt template from prompts.properties
	 * @return assistant prompt template
	 */
	@PropertyKey(resourceBundle = PROMPTS_FQN)
	String assistantTemplate() default "";

	/**
	 * function names annotated by @GPTFunction annotation
	 * @return function names
	 */
	String[] functions() default {};

}
