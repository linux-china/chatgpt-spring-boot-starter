package org.mvnsearch.chatgpt.demo.service;

import org.mvnsearch.chatgpt.model.ChatCompletion;
import org.mvnsearch.chatgpt.model.ChatGPTExchange;
import reactor.core.publisher.Mono;

@ChatGPTExchange
public interface GPTHelloService {

    @ChatCompletion("You are a language translator, please translate the below text to Chinese.\n")
    Mono<String> translate(String text);
}
