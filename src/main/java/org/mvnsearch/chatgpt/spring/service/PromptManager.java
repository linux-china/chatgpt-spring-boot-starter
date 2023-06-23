package org.mvnsearch.chatgpt.spring.service;

import org.jetbrains.annotations.PropertyKey;


public interface PromptManager {
    String PROMPTS_FQN = "prompts";

    String prompt(@PropertyKey(resourceBundle = PROMPTS_FQN) String key, Object... params);
}
