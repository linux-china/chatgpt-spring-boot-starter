package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.demo.service.GPTHelloService;
import org.springframework.beans.factory.annotation.Autowired;


public class GPTHelloServiceTest extends ProjectBootBaseTest {
    @Autowired
    private GPTHelloService helloService;

    @Test
    public void testTranslate() throws Exception {
        System.out.println(helloService.translate("Hello").block());
    }
}
