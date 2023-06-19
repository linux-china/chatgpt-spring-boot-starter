package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.model.ChatRequestBuilder;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
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
    public void testExecuteSQLQuery() throws Exception {
        String context = "You are SQL developer. Write SQL according to requirements, and execute it in MySQL database.";
        final String prompt = "Query all employees whose salary is greater than the average.";
        final ChatCompletionRequest request = ChatRequestBuilder.of(context, prompt)
                .function("execute_sql_query")
                .build();
        System.out.println(GPTFunctionUtils.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request));
        // final ChatCompletionResponse response = chatGPTService.chat(request).block();
        // System.out.println(response.getReplyCombinedText());
    }


    @Test
    public void testChatWithFunctions() throws Exception {
        final String prompt = "Hi Jackie, could you write an email to Libing(libing.chen@gmail.com) and Sam(linux_china@hotmail.com) and invite them to join Mike's birthday party at 4 pm tomorrow? Thanks!";
        final ChatCompletionRequest request = ChatRequestBuilder.of(prompt)
                .function("send_email")
                .build();
        final ChatCompletionResponse response = chatGPTService.chat(request).block();
        // display reply combined text with function call
        System.out.println(response.getReplyCombinedText());
        // call function manually
        /*for (ChatMessage chatMessage : response.getReply()) {
            final FunctionCall functionCall = chatMessage.getFunctionCall();
            if (functionCall != null) {
                final Object result = functionCall.getFunctionStub().call();
                System.out.println(result);
            }
        }*/
    }
}
