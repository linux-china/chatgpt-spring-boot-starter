package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.lang.annotation.*;

/**
 * ChatGPTExchange annotation, almost like HttpExchange
 *
 * @author linux_china
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPTExchange {
    String value() default "";

    @JsonAlias("value")
    String model() default "";

    double temperature() default -1;

    int maxTokens() default -1;

    String[] functions() default {};
}
