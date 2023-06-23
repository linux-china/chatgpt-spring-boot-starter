package org.mvnsearch.chatgpt.spring.config;

import org.mvnsearch.chatgpt.model.*;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.mvnsearch.chatgpt.spring.exchange.ChatGPTServiceProxyFactory;
import org.mvnsearch.chatgpt.spring.http.OpenAIChatAPI;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.PromptManager;
import org.mvnsearch.chatgpt.spring.service.PromptStore;
import org.mvnsearch.chatgpt.spring.service.impl.ChatGPTServiceImpl;
import org.mvnsearch.chatgpt.spring.service.impl.PromptManagerImpl;
import org.mvnsearch.chatgpt.spring.service.impl.PromptsStoreImpl;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
public class ChatGPTAutoConfiguration {

    @Bean
    public OpenAIChatAPI openAIChatAPI(@Value("${openai.api.key}") String openaiApiKey,
                                       @Value("${openai.api.url:https://api.openai.com/v1}") final String openaiApiUrl) throws Exception {
        URL url = new URL(openaiApiUrl);
        String baseUrl = openaiApiUrl;
        if (openaiApiUrl.contains("/chat/")) {
            baseUrl = openaiApiUrl.substring(0, openaiApiUrl.lastIndexOf("/chat/"));
        }
        WebClient webClient;
        if (url.getHost().contains("openai.azure.com")) {
            String apiVersion = openaiApiUrl.substring(openaiApiUrl.lastIndexOf("api-version=") + 12);
            if (apiVersion.contains("&")) {
                apiVersion = apiVersion.substring(0, apiVersion.indexOf("&"));
            }
            final String apiVersionValue = apiVersion;
            // append api-version parameter for url
            ExchangeFilterFunction appendVersionParamFilter = (clientRequest, nextFilter) -> {
                String oldUrl = clientRequest.url().toString();
                URI newUrl = URI.create(oldUrl + "?api-version=" + apiVersionValue);
                ClientRequest filteredRequest = ClientRequest.from(clientRequest).url(newUrl).build();
                return nextFilter.exchange(filteredRequest);
            };
            webClient = WebClient.builder()
                    .defaultHeader("api-key", openaiApiKey)
                    .baseUrl(baseUrl)
                    .filter(appendVersionParamFilter)
                    .build();
        } else {
            webClient = WebClient.builder()
                    .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                    .baseUrl(baseUrl)
                    .build();
        }
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builder().clientAdapter(WebClientAdapter.forClient(webClient)).build();
        return httpServiceProxyFactory.createClient(OpenAIChatAPI.class);

    }

    @Bean
    public PromptStore promptsPropertiesStore() {
        return new PromptsStoreImpl();
    }

    @Bean
    public PromptManager promptManager(List<PromptStore> promptsStores) throws Exception {
        return new PromptManagerImpl(promptsStores);
    }

    @Bean
    public ChatGPTService chatGPTService(OpenAIChatAPI openAIChatAPI,
                                         Optional<List<GPTFunctionsStub>> stubs) throws Exception {
        return new ChatGPTServiceImpl(openAIChatAPI, stubs.orElse(Collections.emptyList()));
    }

    @Bean
    public ChatGPTServiceProxyFactory chatGPTServiceProxyFactory(ChatGPTService chatGPTService) {
        return new ChatGPTServiceProxyFactory(chatGPTService);
    }
}
