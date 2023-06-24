package org.mvnsearch.chatgpt.spring.exchange;

import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.PromptManager;

import java.lang.reflect.Proxy;

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
