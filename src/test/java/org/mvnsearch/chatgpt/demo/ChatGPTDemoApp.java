package org.mvnsearch.chatgpt.demo;

import org.mvnsearch.chatgpt.demo.service.GPTHelloService;
import org.mvnsearch.chatgpt.spring.client.ChatGPTServiceProxyFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChatGPTDemoApp {

	public static void main(String[] args) {
		SpringApplication.run(ChatGPTDemoApp.class, args);
	}

	@Bean
	GPTHelloService gptHelloService(ChatGPTServiceProxyFactory proxyFactory) {
		return proxyFactory.createClient(GPTHelloService.class);
	}

}
