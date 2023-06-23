package org.mvnsearch.chatgpt.spring.service.impl;

import org.mvnsearch.chatgpt.spring.service.PromptStore;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class PromptPropertiesStoreImpl implements PromptStore {
    @Override
    public Map<String, String> readAll() {
        Map<String, String> prompts = new HashMap<>();
        try {
            ClassPathResource classPathResource = new ClassPathResource("/prompts.properties");
            if (classPathResource.exists()) {
                Properties properties = new Properties();
                properties.load(classPathResource.getInputStream());
                properties.forEach((key, value) -> prompts.put(key.toString(), value.toString()));
            }
        } catch (Exception ignore) {
        }
        return prompts;
    }
}
