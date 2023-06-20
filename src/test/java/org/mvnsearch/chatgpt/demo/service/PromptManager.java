package org.mvnsearch.chatgpt.demo.service;

import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class PromptManager {
    private static final String BUNDLE_FQN = "demo.prompts";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_FQN);

    public static String prompt(@PropertyKey(resourceBundle = BUNDLE_FQN) String key, Object... params) {
        String value;
        try {
            value = RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException ignore) {
            value = "!!!" + key + "!!!";
        }
        if (params != null && params.length > 0 && value.indexOf('{') >= 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
    }

}
