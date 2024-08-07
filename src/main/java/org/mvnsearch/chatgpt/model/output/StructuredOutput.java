package org.mvnsearch.chatgpt.model.output;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StructuredOutput {

	String name() default "";

	String description() default "";

}
