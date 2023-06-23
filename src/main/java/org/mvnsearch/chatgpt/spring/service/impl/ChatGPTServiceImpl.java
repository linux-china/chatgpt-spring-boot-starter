package org.mvnsearch.chatgpt.spring.service.impl;

import org.jetbrains.annotations.PropertyKey;
import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.spring.http.OpenAIChatAPI;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.PromptManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mvnsearch.chatgpt.spring.service.PromptManager.PROMPTS_FQN;

public class ChatGPTServiceImpl implements ChatGPTService {
    private static final Logger log = LoggerFactory.getLogger(ChatGPTServiceImpl.class);
    private final OpenAIChatAPI openAIChatAPI;
    private final PromptManager promptManager;
    private String model = "gpt-3.5-turbo";
    private final Map<String, ChatGPTJavaFunction> allJsonSchemaFunctions = new HashMap<>();
    private final Map<String, ChatFunction> allChatFunctions = new HashMap<>();

    public ChatGPTServiceImpl(OpenAIChatAPI openAIChatAPI, PromptManager promptManager, List<GPTFunctionsStub> stubs) throws Exception {
        this.openAIChatAPI = openAIChatAPI;
        this.promptManager = promptManager;
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

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public Mono<ChatCompletionResponse> chat(ChatCompletionRequest request) {
        if (request.getModel() == null) {
            request.setModel(model);
        }
        injectFunctions(request);
        request.setStream(null);
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
        if (request.getModel() == null) {
            request.setModel(model);
        }
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

    @Override
    public <T> Function<T, Mono<String>> promptAsFunction(@PropertyKey(resourceBundle = PROMPTS_FQN) String promptKey) {
        return obj -> {
            String prompt;
            if (obj != null) {
                if (obj.getClass().isArray()) { //array
                    Object[] args = (Object[]) obj;
                    prompt = promptManager.prompt(promptKey, args);
                } else if (obj instanceof List<?> list) {  // list
                    prompt = promptManager.prompt(promptKey, list.toArray());
                } else if (obj.getClass().isRecord()) { // record
                    RecordComponent[] fields = obj.getClass().getRecordComponents();
                    Object[] args = new Object[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        try {
                            final Object value = fields[i].getAccessor().invoke(obj);
                            args[i] = value;
                        } catch (Exception ignore) {
                            args[i] = null;
                        }
                    }
                    prompt = promptManager.prompt(promptKey, args);
                } else {  //object
                    prompt = promptManager.prompt(promptKey, obj);
                }
            } else {
                prompt = promptManager.prompt(promptKey);
            }
            final ChatCompletionRequest request = ChatRequestBuilder.of(prompt).model(model).build();
            return chat(request).map(ChatCompletionResponse::getReplyText);
        };
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
        request.updateModelWithFunctionSupport();
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
