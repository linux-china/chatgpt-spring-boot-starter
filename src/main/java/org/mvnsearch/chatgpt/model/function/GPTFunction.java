package org.mvnsearch.chatgpt.model.function;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPTFunction {

	String value() default "";

	String name() default "";

}
