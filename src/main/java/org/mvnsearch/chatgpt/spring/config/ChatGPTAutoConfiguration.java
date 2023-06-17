package org.mvnsearch.chatgpt.spring.config;

import org.mvnsearch.chatgpt.model.function.GPTFunctionsStub;
import org.mvnsearch.chatgpt.spring.http.OpenAIChatAPI;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.ChatGPTServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AutoConfiguration
public class ChatGPTAutoConfiguration {

    @Bean
    public OpenAIChatAPI openAIChatAPI(@Value("${openai.api.key}") String openaiApiKey) {
        WebClient webClient = WebClient.builder().defaultHeader("Authorization", "Bearer " + openaiApiKey).build();
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builder().clientAdapter(WebClientAdapter.forClient(webClient)).build();
        return httpServiceProxyFactory.createClient(OpenAIChatAPI.class);

    }

    @Bean
    public ChatGPTService chatGPTService(OpenAIChatAPI openAIChatAPI,
                                         Optional<List<GPTFunctionsStub>> stubs) throws Exception {
        return new ChatGPTServiceImpl(openAIChatAPI, stubs.orElse(Collections.emptyList()));
    }
}
