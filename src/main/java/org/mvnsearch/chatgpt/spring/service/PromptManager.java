package org.mvnsearch.chatgpt.spring.service;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import java.util.Map;


public interface PromptManager {
    String PROMPTS_FQN = "prompts";

    String prompt(@PropertyKey(resourceBundle = PROMPTS_FQN) String key, Object... params);


    Map<String, String> getAllPrompts();

    @Nullable
    String getRawPrompt(String key);

    boolean containsPrompt(String key);
}
