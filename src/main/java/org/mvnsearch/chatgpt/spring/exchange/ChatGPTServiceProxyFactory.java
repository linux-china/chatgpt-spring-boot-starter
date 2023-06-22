package org.mvnsearch.chatgpt.spring.exchange;

import org.mvnsearch.chatgpt.spring.service.ChatGPTService;

import java.lang.reflect.Proxy;

public class ChatGPTServiceProxyFactory {

    private final ChatGPTService chatGPTService;

    public ChatGPTServiceProxyFactory(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @SuppressWarnings("unchecked")
    public <T> T createClient(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new GptExchangeInvocationHandler(chatGPTService, clazz));
    }

}
