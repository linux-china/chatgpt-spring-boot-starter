package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.ChatCompletionResponse;
import org.mvnsearch.chatgpt.model.ChatRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class ChatGPTServiceImplTest extends ProjectBootBaseTest {

	@Autowired
	private ChatGPTService chatGPTService;

	@Autowired
	private PromptManager promptManager;

	@Test
	void testSimpleChat() {
		ChatCompletionRequest request = ChatCompletionRequest.of("What's Java Language?");
		ChatCompletionResponse response = chatGPTService.chat(request).block();
		System.out.println(response.getReplyText());
	}

	@Test
	void testExecuteSQLQuery() throws Exception {
		String prompt = "Query all employees whose salary is greater than the average.";
		ChatCompletionRequest request = ChatRequestBuilder.of(promptManager.prompt("sql-writer", prompt))
			.function("execute_sql_query")
			.build();
		String result = chatGPTService.chat(request).flatMap(ChatCompletionResponse::getReplyCombinedText).block();
		System.out.println(result);
	}

	@Test
	void testChatWithFunctions() throws Exception {
		String prompt = "Hi Jackie, could you write an email to Libing(libing.chen@gmail.com) and Sam(linux_china@hotmail.com) and invite them to join Mike's birthday party at 4 pm tomorrow? Thanks!";
		ChatCompletionRequest request = ChatRequestBuilder.of(prompt).function("send_email").build();
		ChatCompletionResponse response = chatGPTService.chat(request).block();
		// display reply combined text with function call
		System.out.println(response.getReplyCombinedText().block());
		// call function manually
		/*
		 * for (ChatMessage chatMessage : response.getReply()) { final FunctionCall
		 * functionCall = chatMessage.getFunctionCall(); if (functionCall != null) { final
		 * Object result = functionCall.getFunctionStub().call();
		 * System.out.println(result); } }
		 */
	}

	@Test
	void testSmartSpeaker() throws Exception {
		String prompt = "You are a smart speaker, and your name is Alexa. You can play music songs, answer questions and so on. \nAlexa, please play Hotel California.";
		ChatCompletionRequest request = ChatRequestBuilder.of(prompt).function("play_songs").build();
		ChatCompletionResponse response = chatGPTService.chat(request).block();
		// display reply combined text with function call
		System.out.println(response.getReplyCombinedText().block());
	}

	@Test
	void testPromptAsFunction() {
		Function<String, Mono<String>> translateIntoChineseFunction = chatGPTService
			.promptAsLambda("translate-into-chinese");
		Function<String, Mono<String>> sendEmailFunction = chatGPTService.promptAsLambda("send-email", "send_email");
		String result = Mono.just(
				"Hi Jackie, could you write an email to Libing(libing.chen@exaple.com) and Sam(linux_china@example.com) and invite them to join Mike's birthday party at 4 pm tomorrow? Thanks!")
			.flatMap(translateIntoChineseFunction)
			.flatMap(sendEmailFunction)
			.block();
		System.out.println(result);
	}

	record TranslateRequest(String from, String to, String text) {
	}

	@Test
	void testLambdaWithRecord() {
		Function<TranslateRequest, Mono<String>> translateFunction = chatGPTService.promptAsLambda("translate");
		String result = Mono.just(new TranslateRequest("Chinese", "English", "你好！")).flatMap(translateFunction).block();
		System.out.println(result);
	}

	@Test
	void testLambdaWithFunctionResult() {
		Function<String, Mono<List<String>>> executeSqlQuery = chatGPTService.promptAsLambda("sql-writer",
				"execute_sql_query");
		List<String> result = Mono.just("Query all employees whose salary is greater than the average.")
			.flatMap(executeSqlQuery)
			.block();
		assertThat(result).isNotEmpty();
	}

}
