package org.mvnsearch.chatgpt.model;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChatCompletion {
    String value() default "";

    String[] functions() default {};
}
