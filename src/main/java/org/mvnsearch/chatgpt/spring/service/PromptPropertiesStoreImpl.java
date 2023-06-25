package org.mvnsearch.chatgpt.spring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * load prompts from all prompts.properties files in classpath
 *
 * @author linux_china
 */
class PromptPropertiesStoreImpl implements PromptStore {

	private static final Logger log = LoggerFactory.getLogger(PromptPropertiesStoreImpl.class);

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
			properties.forEach((key, value) -> {
				String promptText = value.toString();
				try {
					prompts.put(key.toString(), resolvePromptText(promptText));
				}
				catch (Exception e) {
					log.error("Failed to resolve prompt text: " + promptText, e);
				}
			});
		}
		catch (Exception ignore) {
		}
		return prompts;
	}

	public String resolvePromptText(String value) throws Exception {
		if (value.startsWith("classpath://")) {
			ClassPathResource resource = new ClassPathResource(value.substring(12));
			return new String(resource.getContentAsByteArray(), StandardCharsets.UTF_8);
		}
		else if (value.startsWith("http://") || value.startsWith("https://")) {
			UrlResource resource = new UrlResource(value);
			return new String(resource.getContentAsByteArray(), StandardCharsets.UTF_8);
		}
		else {
			return value;
		}
	}

}
