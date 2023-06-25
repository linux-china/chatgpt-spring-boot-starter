package org.mvnsearch.chatgpt.spring.client;

import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.PromptManager;

import java.lang.reflect.Proxy;

//todo  this should use Spring's ProxyFactoryBean so
//		that the resulting proxy automatically works in AOT/GraalVM.
public class ChatGPTServiceProxyFactory {

	private final ChatGPTService chatGPTService;

	private final PromptManager promptManager;

	public ChatGPTServiceProxyFactory(ChatGPTService chatGPTService, PromptManager promptManager) {
		this.chatGPTService = chatGPTService;
		this.promptManager = promptManager;
	}

	@SuppressWarnings("unchecked")
	public <T> T createClient(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz },
				new GptExchangeInvocationHandler(chatGPTService, promptManager, clazz));
	}

}
