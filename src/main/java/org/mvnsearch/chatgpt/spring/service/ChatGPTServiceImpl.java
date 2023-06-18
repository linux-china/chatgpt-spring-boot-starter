package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.spring.http.OpenAIChatAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatGPTServiceImpl implements ChatGPTService {
    private static final Logger log = LoggerFactory.getLogger(ChatGPTServiceImpl.class);
    private final OpenAIChatAPI openAIChatAPI;
    private final Map<String, ChatGPTJavaFunction> allJsonSchemaFunctions = new HashMap<>();
    private final Map<String, ChatFunction> allChatFunctions = new HashMap<>();

    public ChatGPTServiceImpl(OpenAIChatAPI openAIChatAPI, List<GPTFunctionsStub> stubs) throws Exception {
        this.openAIChatAPI = openAIChatAPI;
        for (GPTFunctionsStub functionStub : stubs) {
            final Map<String, ChatGPTJavaFunction> functions = GPTFunctionUtils.extractFunctions(functionStub.getClass());
            if (!functions.isEmpty()) {
                for (Map.Entry<String, ChatGPTJavaFunction> entry : functions.entrySet()) {
                    final ChatGPTJavaFunction jsonSchemaFunction = entry.getValue();
                    //set spring bean as target
                    jsonSchemaFunction.setTarget(functionStub);
                    allJsonSchemaFunctions.put(entry.getKey(), jsonSchemaFunction);
                    allChatFunctions.put(entry.getKey(), jsonSchemaFunction.toChatFunction());
                }
            }
        }
        log.info("ChatGPTService initialized with {} functions", allJsonSchemaFunctions.size());
    }

    @Override
    public Mono<ChatCompletionResponse> chat(ChatCompletionRequest request) {
        injectFunctions(request);
        boolean functionsIncluded = request.getFunctions() != null;
        if (!functionsIncluded) {
            return openAIChatAPI.chat(request);
        } else {
            return openAIChatAPI.chat(request).doOnNext(response -> {
                for (ChatMessage chatMessage : response.getReply()) {
                    injectFunctionCallLambda(chatMessage);
                }
            });
        }
    }

    @Override
    public Flux<ChatCompletionResponse> stream(ChatCompletionRequest request) {
        request.setStream(true);
        injectFunctions(request);
        boolean functionsIncluded = request.getFunctions() != null;
        if (!functionsIncluded) {
            return openAIChatAPI.stream(request).onErrorContinue((e, obj) -> {
            });
        } else {
            return openAIChatAPI.stream(request)
                    .onErrorContinue((e, obj) -> {
                    })
                    .doOnNext(response -> {
                        for (ChatMessage chatMessage : response.getReply()) {
                            injectFunctionCallLambda(chatMessage);
                        }
                    });
        }
    }

    private void injectFunctions(ChatCompletionRequest request) {
        final List<String> functionNames = request.getFunctionNames();
        if (functionNames != null && !functionNames.isEmpty()) {
            for (String functionName : functionNames) {
                ChatFunction chatFunction = allChatFunctions.get(functionName);
                if (chatFunction != null) {
                    request.addFunction(chatFunction);
                }
            }
        }
    }

    private void injectFunctionCallLambda(ChatMessage chatMessage) {
        final FunctionCall functionCall = chatMessage.getFunctionCall();
        if (functionCall != null) {
            final String functionName = functionCall.getName();
            ChatGPTJavaFunction jsonSchemaFunction = allJsonSchemaFunctions.get(functionName);
            if (jsonSchemaFunction != null) {
                functionCall.setFunctionStub(() -> jsonSchemaFunction.call(functionCall.getArguments())
                );
            }
        }
    }
}
