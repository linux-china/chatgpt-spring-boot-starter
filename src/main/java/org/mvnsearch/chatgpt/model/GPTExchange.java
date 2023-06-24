package org.mvnsearch.chatgpt.model;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * ChatGPTExchange annotation, almost like HttpExchange
 *
 * @author linux_china
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPTExchange {

	String value() default "";

	@AliasFor("value")
	String model() default "";

	double temperature() default -1;

	int maxTokens() default -1;

	String[] functions() default {};

}
