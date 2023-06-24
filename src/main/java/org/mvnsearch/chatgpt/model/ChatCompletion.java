package org.mvnsearch.chatgpt.model;

import org.jetbrains.annotations.PropertyKey;

import java.lang.annotation.*;

import static org.mvnsearch.chatgpt.spring.service.PromptManager.PROMPTS_FQN;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChatCompletion {

	String value() default "";

	String system() default "";

	String assistant() default "";

	@PropertyKey(resourceBundle = PROMPTS_FQN)
	String userTemplate() default "";

	@PropertyKey(resourceBundle = PROMPTS_FQN)
	String systemTemplate() default "";

	@PropertyKey(resourceBundle = PROMPTS_FQN)
	String assistantTemplate() default "";

	String[] functions() default {};

}
