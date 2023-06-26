package org.mvnsearch.chatgpt.spring.client;

import org.mvnsearch.chatgpt.model.GPTExchange;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@RegisterReflectionForBinding({ GPTExchange.class })
class ChatGPTServiceProxyFactoryAutoConfiguration {

	@Bean
	static GPTExchangeBeanRegistrationAotProcessor gptExchangeBeanRegistrationAotProcessor() {
		return new GPTExchangeBeanRegistrationAotProcessor();
	}

}
