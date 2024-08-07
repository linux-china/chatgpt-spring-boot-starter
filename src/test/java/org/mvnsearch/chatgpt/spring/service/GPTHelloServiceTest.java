package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.demo.service.GPTHelloService;
import org.mvnsearch.chatgpt.demo.service.JavaExample;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Locale;

class GPTHelloServiceTest extends ProjectBootBaseTest {

	@Autowired
	private GPTHelloService helloService;

	@Test
	void testTranslateIntoChinese() {
		StepVerifier.create(helloService.translateIntoChinese("Hello").map(result -> !result.isEmpty()))
			.expectNext(true)
			.verifyComplete();
	}

	@Test
	public void testCompletePoem() {
		StepVerifier.create(helloService.completePoem("春眠不觉晓，")).expectNextMatches(result -> {
			System.out.println(result);
			return !result.isEmpty();
		}).verifyComplete();
	}

	@Test
	public void testUnaryTranslate() {
		GPTHelloService.TranslateRequest request = new GPTHelloService.TranslateRequest("Chinese", "English", "你好！");
		StepVerifier.create(helloService.unaryTranslate(request).map(result -> result.contains("Hello")))
			.expectNext(true)
			.verifyComplete();
	}

	@Test
	void testTranslate() {
		StepVerifier.create(helloService.translate("Chinese", "English", "你好！"))
			.expectNextMatches(result -> result.toLowerCase(Locale.ENGLISH).contains("hello"))
			.verifyComplete();

	}

	@Test
	void testTranslateFromTemplate() {
		StepVerifier.create(helloService.translateFromTemplate("Chinese", "English", "你好！"))
			.expectNextMatches(result -> result.toLowerCase(Locale.ENGLISH).contains("hello"))
			.verifyComplete();
	}


	@Test
	void testStructuredOutput() {
		final JavaExample example = helloService.generateJavaExample("Write a simple JUnit 5 example.").block();
		System.out.println(example.code());
	}

}
