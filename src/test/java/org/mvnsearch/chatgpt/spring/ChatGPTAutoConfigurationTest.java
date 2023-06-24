package org.mvnsearch.chatgpt.spring;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

class ChatGPTAutoConfigurationTest extends ProjectBootBaseTest {

	@Autowired
	private ChatGPTProperties properties;

	@Test
	void properties() {
		Assertions.assertTrue(StringUtils.hasText(properties.api().key()), "the api key must be non-null");
	}

}