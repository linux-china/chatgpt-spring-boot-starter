package org.mvnsearch.chatgpt.spring.service.impl;

import org.jetbrains.annotations.PropertyKey;
import org.mvnsearch.chatgpt.spring.service.PromptManager;
import org.mvnsearch.chatgpt.spring.service.PromptStore;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromptManagerImpl implements PromptManager {
    private final Map<String, String> allPrompts = new HashMap<>();

    public PromptManagerImpl(List<PromptStore> promptStores) throws Exception {
        for (PromptStore promptStore : promptStores) {
            allPrompts.putAll(promptStore.readAll());
        }
    }

    public String prompt(@PropertyKey(resourceBundle = PROMPTS_FQN) String key, Object... params) {
        if (allPrompts.containsKey(key)) {
            String prompt = allPrompts.get(key);
            if (params != null && params.length > 0 && prompt.indexOf('{') >= 0) {
                prompt = MessageFormat.format(prompt, params);
            }
            return prompt;
        } else {
            return "!!!" + key + "!!!";
        }
    }

}
