package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.demo.service.GPTHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class GPTHelloServiceTest extends ProjectBootBaseTest {

	@Autowired
	private GPTHelloService helloService;

	@Test
	public void testTranslateIntoChinse() throws Exception {
		System.out.println(helloService.translateIntoChinese("Hello").block());
	}

	@Test
	public void testTranslate() throws Exception {
		final Mono<String> translate = helloService.translate("Chinese", "English", "你好！");
		System.out.println(translate.block());
	}

	@Test
	public void testTranslateFromTemplate() throws Exception {
		System.out.println(helloService.translateFromTemplate("Chinese", "English", "你好！").block());
	}

}
