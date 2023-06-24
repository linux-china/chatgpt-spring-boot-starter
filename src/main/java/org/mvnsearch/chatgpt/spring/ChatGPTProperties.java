package org.mvnsearch.chatgpt.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record ChatGPTProperties(Api api) {

	public record Api(String key, String url) {
	}
}
