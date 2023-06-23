package org.mvnsearch.chatgpt.spring.service.impl;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.spring.service.PromptManager;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


public class PromptManagerImplTest extends ProjectBootBaseTest {
    @Autowired
    private PromptManager promptManager;

    @Test
    public void testLoadPrompt() {
        assertThat(promptManager.prompt("sql-developer", "Query all employees.")).contains("SQL");
        assertThat(promptManager.prompt("translate", "Chinese", "English", "你好！")).contains("Chinese");
    }
}
