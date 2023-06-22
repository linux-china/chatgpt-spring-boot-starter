package org.mvnsearch.chatgpt.spring.exchange;

import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;


public class GptExchangeInvocationHandler implements InvocationHandler {
    private final ChatGPTService chatGPTService;
    private final Class<?> interfaceClass;
    private final ChatGPTExchange gptExchange;

    public GptExchangeInvocationHandler(ChatGPTService chatGPTService, Class<?> interfaceClass) {
        this.chatGPTService = chatGPTService;
        this.interfaceClass = interfaceClass;
        this.gptExchange = interfaceClass.getAnnotation(ChatGPTExchange.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String prompt;
        String[] functions = null;
        final ChatCompletion chatCompletionAnnotation = method.getAnnotation(ChatCompletion.class);
        if (chatCompletionAnnotation != null) {
            prompt = chatCompletionAnnotation.value();
            if (args != null && args.length > 0) {
                if (prompt.contains("{") && prompt.contains("}")) {
                    prompt = MessageFormat.format(prompt, args);
                } else {
                    StringBuilder sb = new StringBuilder(prompt);
                    for (Object arg : args) {
                        if (arg != null) {
                            sb.append(" ").append(arg);
                        }
                    }
                    prompt = sb.toString();
                }
            }
            functions = chatCompletionAnnotation.functions();
        } else {
            prompt = method.getName();
            prompt = prompt.replaceAll("([A-Z])", " $1").trim();
        }
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.addMessage(ChatMessage.userMessage(prompt));
        // inject global configuration
        if (gptExchange != null) {
            if ((functions == null || functions.length == 0)) {
                functions = gptExchange.functions();
            }
            if (gptExchange.temperature() > 0) {
                request.setTemperature(gptExchange.temperature());
            }
            if (gptExchange.maxTokens() > 0) {
                request.setMaxTokens(gptExchange.maxTokens());
            }
            if (gptExchange.value() != null && !gptExchange.value().isEmpty()) {
                request.setModel(gptExchange.value());
            }
        }
        if (functions != null && functions.length > 0) {
            request.setFunctionNames(Arrays.stream(functions).toList());
        }
        return chatGPTService.chat(request).map(ChatCompletionResponse::getReplyCombinedText);
    }
}
