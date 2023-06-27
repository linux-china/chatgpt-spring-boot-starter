package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class PromptManagerImplTest extends ProjectBootBaseTest {

	@Autowired
	private PromptManager promptManager;

	@Test
	void testLoadPrompt() {
		assertThat(promptManager.prompt("sql-writer", "Query all employees.")).contains("SQL");
		assertThat(promptManager.prompt("translate", "Chinese", "English", "你好！")).contains("Chinese");
	}

	@Test
	public void testPromptValueAsUrl() {
		final String prompt = promptManager.prompt("conversation", "My name is Jackie", "");
		assertThat(prompt).contains("Current conversation");
		System.out.println(prompt);
	}

}
