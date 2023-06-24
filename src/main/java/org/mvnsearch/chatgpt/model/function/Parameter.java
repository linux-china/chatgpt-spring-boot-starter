package org.mvnsearch.chatgpt.model.function;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Parameter {

	String value() default "";

	String name() default "";

	boolean required() default false;

}
