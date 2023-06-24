package org.mvnsearch.chatgpt.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(ChatGPTProperties.class)
class ChatGPTAutoConfiguration {

}
