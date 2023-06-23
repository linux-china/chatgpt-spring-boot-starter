package org.mvnsearch.chatgpt.demo.service;

import org.mvnsearch.chatgpt.model.ChatCompletion;
import org.mvnsearch.chatgpt.model.GPTExchange;
import reactor.core.publisher.Mono;

@GPTExchange
public interface GPTHelloService {

    @ChatCompletion("You are a language translator, please translate the below text to Chinese.\n")
    Mono<String> translateIntoChinese(String text);

    @ChatCompletion("You are a language translator, please translate the below text from {0} to {1}.\n {2}")
    Mono<String> translate(String sourceLanguage, String targetLanguage, String text);

    @ChatCompletion(userTemplate = "translate")
    Mono<String> translateFromTemplate(String sourceLanguage, String targetLanguage, String text);
}
