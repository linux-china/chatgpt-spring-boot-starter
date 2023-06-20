package org.mvnsearch.chatgpt.demo.service;

import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class PromptManager {
    private static final String BUNDLE_FQN = "demo.prompts";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_FQN);

    public static String prompt(@PropertyKey(resourceBundle = BUNDLE_FQN) String key, Object... params) {
        if (RESOURCE_BUNDLE.containsKey(key)) {
            String prompt = RESOURCE_BUNDLE.getString(key);
            if (params != null && params.length > 0 && prompt.indexOf('{') >= 0) {
                prompt = MessageFormat.format(prompt, params);
            }
            return prompt;
        } else {
            return "!!!" + key + "!!!";
        }
    }

}
