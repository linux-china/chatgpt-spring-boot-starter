package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.model.ChatMessage;
import org.mvnsearch.chatgpt.model.FunctionCall;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.model.function.JsonSchemaFunction;
import org.mvnsearch.chatgpt.spring.http.OpenAIChatAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatGPTServiceImpl implements ChatGPTService {
    private static final Logger log = LoggerFactory.getLogger(ChatGPTServiceImpl.class);
    private final OpenAIChatAPI openAIChatAPI;
    private final Map<String, JsonSchemaFunction> functionsMap = new HashMap<>();
    private final Map<String, GPTFunctionsStub> functionBeans = new HashMap<>();

    public ChatGPTServiceImpl(OpenAIChatAPI openAIChatAPI, List<GPTFunctionsStub> stubs) throws Exception {
        this.openAIChatAPI = openAIChatAPI;
        for (GPTFunctionsStub functionStub : stubs) {
            final Map<String, JsonSchemaFunction> functions = GPTFunctionUtils.extractFunctions(functionStub.getClass());
            if (!functions.isEmpty()) {
                functionsMap.putAll(functions);
                for (Map.Entry<String, JsonSchemaFunction> entry : functions.entrySet()) {
                    functionBeans.put(entry.getKey(), functionStub);
                }
            }
        }
        log.info("ChatGPTService initialized with {} functions", functionsMap.size());
    }

    @Override
    public Mono<ChatCompletionResponse> chat(ChatCompletionRequest request) {
        final List<String> functionNames = request.getFunctionNames();
        if (functionNames != null && !functionNames.isEmpty()) {
            List<JsonSchemaFunction> functions = new ArrayList<>();
            for (String functionName : functionNames) {
                JsonSchemaFunction function = functionsMap.get(functionName);
                if (function != null) {
                    functions.add(function);
                }
            }
            try {
                request.setFunctions(GPTFunctionUtils.toFunctionsJsonArray(functions));
            } catch (Exception ignore) {

            }
        }
        boolean functionsIncluded = request.getFunctions() != null;
        if (!functionsIncluded) {
            return openAIChatAPI.chat(request);
        } else {
            return openAIChatAPI.chat(request).doOnNext(response -> {
                for (ChatMessage chatMessage : response.getReply()) {
                    final FunctionCall functionCall = chatMessage.getFunctionCall();
                    if (functionCall != null) {
                        final String functionName = functionCall.getName();
                        JsonSchemaFunction jsonSchemaFunction = functionsMap.get(functionName);
                        if (jsonSchemaFunction != null) {
                            functionCall.setFunctionStub(() -> GPTFunctionUtils.callGPTFunction(
                                    functionBeans.get(functionName), jsonSchemaFunction, functionCall.getArguments())
                            );
                        }
                    }
                }
            });
        }
    }
}
