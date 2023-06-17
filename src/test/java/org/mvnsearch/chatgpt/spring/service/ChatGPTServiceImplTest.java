package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.model.ChatMessage;
import org.mvnsearch.chatgpt.model.FunctionCall;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ChatGPTServiceImplTest extends ProjectBootBaseTest {
    @Autowired
    private ChatGPTService chatGPTService;

    @Test
    public void testSimpleChat() {
        final ChatCompletionRequest request = ChatCompletionRequest.of("What's Java Language?");
        final ChatCompletionResponse response = chatGPTService.chat(request).block();
        System.out.println(response.getReplyText());
    }

    @Test
    public void testChatWithFunctions() throws Exception {
        final ChatCompletionRequest request = ChatCompletionRequest.functions("Hi Jackie. If you have time, could you send an email to libing.chen@gmail.com and linux_china@hotmail.com and invite to join the party on tomorrow? Thanks!",
                List.of("send_email"));
        final ChatCompletionResponse response = chatGPTService.chat(request).block();
        // display reply combined text with function call
        System.out.println(response.getReplyCombinedText());
        // call function manually
        for (ChatMessage chatMessage : response.getReply()) {
            final FunctionCall functionCall = chatMessage.getFunctionCall();
            if (functionCall != null) {
                final Object result = functionCall.getFunctionStub().call();
                System.out.println(result);
            }
        }
    }
}
