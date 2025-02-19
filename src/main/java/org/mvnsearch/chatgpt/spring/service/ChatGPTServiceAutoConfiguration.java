package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.CompletionUsage;
import org.mvnsearch.chatgpt.model.completion.chat.*;
import org.mvnsearch.chatgpt.model.completion.completion.CompletionRequest;
import org.mvnsearch.chatgpt.model.completion.completion.CompletionResponse;
import org.mvnsearch.chatgpt.model.embedding.EmbeddingsRequest;
import org.mvnsearch.chatgpt.model.embedding.EmbeddingsResponse;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunction;
import org.mvnsearch.chatgpt.model.function.Parameter;
import org.mvnsearch.chatgpt.model.function.Parameters;
import org.mvnsearch.chatgpt.spring.ChatGPTProperties;
import org.mvnsearch.chatgpt.spring.client.ChatGPTServiceProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.URI;
import java.net.URL;
import java.util.List;

@RegisterReflectionForBinding({ Parameter.class, GPTFunction.class, ChatCompletionRequest.class,
		ChatCompletionResponse.class, ChatCompletionChoice.class, CompletionUsage.class, ChatMessage.class,
		FunctionCall.class, ChatFunction.class, Parameters.class, Parameters.JsonSchemaProperty.class,
		Parameters.JsonArrayItems.class, ChatGPTJavaFunction.class, EmbeddingsRequest.class, EmbeddingsResponse.class,
		CompletionRequest.class, CompletionRequest.class, CompletionResponse.class })
@AutoConfiguration
class ChatGPTServiceAutoConfiguration {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Bean
	static GPTFunctionRegistry registry() {
		return new GPTFunctionRegistry();
	}

	@Bean
	PromptStore promptsPropertiesStore() {
		return new PromptPropertiesStoreImpl();
	}

	@Bean
	PromptManager promptManager(List<PromptStore> promptsStores) throws Exception {
		return new PromptManagerImpl(promptsStores);
	}

	@Bean
	ChatGPTService chatGPTService(ChatGPTProperties properties, OpenAIChatAPI openAIChatAPI,
			PromptManager promptManager, GPTFunctionRegistry registry) throws Exception {
		final ChatGPTServiceImpl chatGPTService = new ChatGPTServiceImpl(openAIChatAPI, promptManager, registry);
		if (properties.model() != null) {
			chatGPTService.setModel(properties.model());
		}
		return chatGPTService;
	}

	@Bean
	ChatGPTServiceProxyFactory chatGPTServiceProxyFactory(ChatGPTService chatGPTService, PromptManager promptManager) {
		return new ChatGPTServiceProxyFactory(chatGPTService, promptManager);
	}

	@Bean
	OpenAIChatAPI openAIChatAPI(ChatGPTProperties properties) throws Exception {
		// noinspection removal
		return HttpServiceProxyFactory.builder()
			.clientAdapter(WebClientAdapter.forClient(openAIWebClient(properties)))
			.build()
			.createClient(OpenAIChatAPI.class);

	}

	@Bean
	OpenAIFileAPI openAIFileAPI(ChatGPTProperties properties) throws Exception {
		// noinspection removal
		return HttpServiceProxyFactory.builder()
			.clientAdapter(WebClientAdapter.forClient(openAIWebClient(properties)))
			.build()
			.createClient(OpenAIFileAPI.class);
	}

	@Bean
	OpenAIBatchAPI openAIBatchAPI(ChatGPTProperties properties) throws Exception {
		// noinspection removal
		return HttpServiceProxyFactory.builder()
			.clientAdapter(WebClientAdapter.forClient(openAIWebClient(properties)))
			.build()
			.createClient(OpenAIBatchAPI.class);
	}

	private WebClient openAIWebClient(ChatGPTProperties properties) throws Exception {
		String openaiApiKey = properties.api().key();
		String openaiApiUrl = StringUtils.hasText(properties.api().url()) ? properties.api().url()
				: "https://api.openai.com/v1";
		URL url = new URL(openaiApiUrl);
		String baseUrl = openaiApiUrl;
		if (openaiApiUrl.contains("/chat/")) {
			baseUrl = openaiApiUrl.substring(0, openaiApiUrl.lastIndexOf("/chat/"));
		}
		WebClient client;
		if (url.getHost().contains("openai.azure.com")) {
			String apiVersion = openaiApiUrl.substring(openaiApiUrl.lastIndexOf("api-version=") + 12);
			if (apiVersion.contains("&")) {
				apiVersion = apiVersion.substring(0, apiVersion.indexOf("&"));
			}
			String apiVersionValue = apiVersion;
			// append api-version parameter for url
			ExchangeFilterFunction appendVersionParamFilter = (clientRequest, nextFilter) -> {
				String oldUrl = clientRequest.url().toString();
				URI newUrl = URI.create(oldUrl + "?api-version=" + apiVersionValue);
				ClientRequest filteredRequest = ClientRequest.from(clientRequest).url(newUrl).build();
				return nextFilter.exchange(filteredRequest);
			};
			client = WebClient.builder()
				.defaultHeader("api-key", openaiApiKey)
				.baseUrl(baseUrl)
				.filter(appendVersionParamFilter)
				.build();
		}
		else {
			client = WebClient.builder()
				.defaultHeader("Authorization", "Bearer " + openaiApiKey)
				.baseUrl(baseUrl)
				.build();
		}
		return client;
	}

}
