package org.mvnsearch.chatgpt.model;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * ChatGPTExchange annotation, almost like HttpExchange
 *
 * @author linux_china
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChatGPTExchange {
    String value() default "gpt-3.5-turbo";

    @AliasFor("value")
    String model() default "gpt-3.5-turbo";

    double temperature() default -1;

    int maxTokens() default -1;

    String[] functions() default {};
}
