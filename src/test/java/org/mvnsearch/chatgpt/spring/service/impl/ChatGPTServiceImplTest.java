package org.mvnsearch.chatgpt.spring.service.impl;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.model.ChatRequestBuilder;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.mvnsearch.chatgpt.spring.service.PromptManager;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class ChatGPTServiceImplTest extends ProjectBootBaseTest {
    @Autowired
    private ChatGPTService chatGPTService;
    @Autowired
    private PromptManager promptManager;

    @Test
    public void testSimpleChat() {
        final ChatCompletionRequest request = ChatCompletionRequest.of("What's Java Language?");
        final ChatCompletionResponse response = chatGPTService.chat(request).block();
        System.out.println(response.getReplyText());
    }

    @Test
    public void testExecuteSQLQuery() throws Exception {
        final String prompt = "Query all employees whose salary is greater than the average.";
        final ChatCompletionRequest request = ChatRequestBuilder.of(promptManager.prompt("sql-developer"), prompt)
                .function("execute_sql_query")
                .build();
        String result = chatGPTService.chat(request).flatMap(ChatCompletionResponse::getReplyCombinedText).block();
        System.out.println(result);
    }


    @Test
    public void testChatWithFunctions() throws Exception {
        final String prompt = "Hi Jackie, could you write an email to Libing(libing.chen@gmail.com) and Sam(linux_china@hotmail.com) and invite them to join Mike's birthday party at 4 pm tomorrow? Thanks!";
        final ChatCompletionRequest request = ChatRequestBuilder.of(prompt)
                .function("send_email")
                .build();
        final ChatCompletionResponse response = chatGPTService.chat(request).block();
        // display reply combined text with function call
        System.out.println(response.getReplyCombinedText().block());
        // call function manually
        /*for (ChatMessage chatMessage : response.getReply()) {
            final FunctionCall functionCall = chatMessage.getFunctionCall();
            if (functionCall != null) {
                final Object result = functionCall.getFunctionStub().call();
                System.out.println(result);
            }
        }*/
    }

    @Test
    public void testPromptLambda() {
        final Function<String[], Mono<String>> translateFunction = chatGPTService.promptLambda("translate");
        final String result = translateFunction.apply(new String[]{"Chinese", "English", "你好！"}).block();
        System.out.println(result);
    }
}
