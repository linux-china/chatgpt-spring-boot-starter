package org.mvnsearch.chatgpt.spring.service.impl;

import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.mvnsearch.chatgpt.spring.http.OpenAIChatAPI;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.PromptManager;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@RegisterReflectionForBinding({
        Parameter.class,
        GPTFunction.class,
        ChatCompletionRequest.class,
        ChatCompletionResponse.class,
        ChatCompletionChoice.class,
        ChatCompletionUsage.class,
        ChatMessage.class,
        FunctionCall.class,
        ChatFunction.class,
        ChatFunction.Parameters.class,
        ChatFunction.JsonSchemaProperty.class,
        ChatFunction.JsonArrayItems.class,
        GPTFunctionsStub.class,
        ChatGPTJavaFunction.class,
})
@AutoConfiguration
class ServiceAutoConfiguration {

    @Bean
    static GPTFunctionRegistry registry() {
        return new GPTFunctionRegistry();
    }

    @Bean
    ChatGPTService chatGPTService(OpenAIChatAPI openAIChatAPI, PromptManager promptManager,
                                  GPTFunctionRegistry registry) throws Exception {
        return new ChatGPTServiceImpl(openAIChatAPI, promptManager, registry);
    }


}
