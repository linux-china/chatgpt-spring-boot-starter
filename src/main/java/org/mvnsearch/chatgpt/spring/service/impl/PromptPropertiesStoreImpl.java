package org.mvnsearch.chatgpt.spring.service.impl;

import org.mvnsearch.chatgpt.spring.service.PromptStore;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * load prompts from all prompts.properties files in classpath
 *
 * @author linux_china
 */
public class PromptPropertiesStoreImpl implements PromptStore {

	@Override
	public Map<String, String> readAll() {
		Map<String, String> prompts = new HashMap<>();
		try {
			Properties properties = new Properties();
			final Enumeration<URL> promptURLs = this.getClass().getClassLoader().getResources("prompts.properties");
			while (promptURLs.hasMoreElements()) {
				try (var stream = promptURLs.nextElement().openStream()) {
					properties.load(stream);
				}
			}
			properties.forEach((key, value) -> prompts.put(key.toString(), value.toString()));
		}
		catch (Exception ignore) {
		}
		return prompts;
	}

}
