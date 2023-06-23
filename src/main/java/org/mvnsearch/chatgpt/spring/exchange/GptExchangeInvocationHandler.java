package org.mvnsearch.chatgpt.spring.exchange;

import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.PromptManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class GptExchangeInvocationHandler implements InvocationHandler {
    private final ChatGPTService chatGPTService;
    private final PromptManager promptManager;
    private final Class<?> interfaceClass;
    private final GPTExchange gptExchangeAnnotation;

    public GptExchangeInvocationHandler(ChatGPTService chatGPTService, PromptManager promptManager, Class<?> interfaceClass) {
        this.chatGPTService = chatGPTService;
        this.promptManager = promptManager;
        this.interfaceClass = interfaceClass;
        this.gptExchangeAnnotation = interfaceClass.getAnnotation(GPTExchange.class);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<ChatMessage> messages = new ArrayList<>();
        String[] functions = null;
        final ChatCompletion chatCompletionAnnotation = method.getAnnotation(ChatCompletion.class);
        if (chatCompletionAnnotation != null) {
            functions = chatCompletionAnnotation.functions();
            // user message
            String userMessage = chatCompletionAnnotation.value();
            if (userMessage.isEmpty() && !chatCompletionAnnotation.userTemplate().isEmpty()) {
                userMessage = promptManager.prompt(chatCompletionAnnotation.userTemplate(), args);
            } else {
                userMessage = formatChatMessage("user", userMessage, args);
            }
            messages.add(ChatMessage.userMessage(userMessage));
            // system message
            String systemMessage = chatCompletionAnnotation.system();
            if (systemMessage.isEmpty() && !chatCompletionAnnotation.systemTemplate().isEmpty()) {
                systemMessage = promptManager.prompt(chatCompletionAnnotation.systemTemplate(), args);
            } else if (!systemMessage.isEmpty()) {
                systemMessage = formatChatMessage("system", systemMessage, args);
            }
            if (!systemMessage.isEmpty()) {
                messages.add(ChatMessage.systemMessage(systemMessage));
            }
            // assistant message
            String assistantMessage = chatCompletionAnnotation.assistant();
            if (assistantMessage.isEmpty() && !chatCompletionAnnotation.assistantTemplate().isEmpty()) {
                assistantMessage = promptManager.prompt(chatCompletionAnnotation.assistantTemplate(), args);
            } else if (!assistantMessage.isEmpty()) {
                assistantMessage = formatChatMessage("assistant", assistantMessage, args);
            }
            if (!assistantMessage.isEmpty()) {
                messages.add(ChatMessage.assistantMessage(assistantMessage));
            }
        } else {
            String userMessage = method.getName();
            userMessage = userMessage.replaceAll("([A-Z])", " $1").trim();
            messages.add(ChatMessage.userMessage(userMessage));
        }
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setMessages(messages);
        // inject global configuration
        if (gptExchangeAnnotation != null) {
            if ((functions == null || functions.length == 0)) {
                functions = gptExchangeAnnotation.functions();
            }
            if (!gptExchangeAnnotation.value().isEmpty()) {
                request.setModel(gptExchangeAnnotation.value());
            }
            if (gptExchangeAnnotation.temperature() >= 0) {
                request.setTemperature(gptExchangeAnnotation.temperature());
            }
            if (gptExchangeAnnotation.maxTokens() > 0) {
                request.setMaxTokens(gptExchangeAnnotation.maxTokens());
            }
            if (gptExchangeAnnotation.value() != null && !gptExchangeAnnotation.value().isEmpty()) {
                request.setModel(gptExchangeAnnotation.value());
            }
            if (functions == null || functions.length == 0) {
                functions = gptExchangeAnnotation.functions();
            }
        }
        if (functions != null && functions.length > 0) {
            request.setFunctionNames(Arrays.stream(functions).toList());
            return chatGPTService.chat(request).map(ChatCompletionResponse::getReplyCombinedText);
        } else {
            return chatGPTService.chat(request).map(ChatCompletionResponse::getReplyText);
        }

    }

    public String formatChatMessage(String role, String content, Object[] args) {
        if (args != null && args.length > 0) {
            if (content.contains("{") && content.contains("}")) {
                content = MessageFormat.format(content, args);
            } else if (Objects.equals(role, "user")) {
                StringBuilder sb = new StringBuilder(content);
                for (Object arg : args) {
                    if (arg != null) {
                        sb.append(" ").append(arg);
                    }
                }
                content = sb.toString();
            }
        }
        return content;
    }
}
