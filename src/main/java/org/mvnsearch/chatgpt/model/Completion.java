package org.mvnsearch.chatgpt.model;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Completion {

	/**
	 * prefix for completion
	 * @return prefix
	 */
	String value() default "";

	String model() default "gpt-3.5-turbo-instruct";

}
