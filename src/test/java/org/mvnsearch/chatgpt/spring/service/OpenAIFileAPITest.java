package org.mvnsearch.chatgpt.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.batch.BatchObject;
import org.mvnsearch.chatgpt.model.batch.CreateBatchRequest;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionBatchRequest;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.file.FileObject;
import org.mvnsearch.chatgpt.model.file.ListFilesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OpenAI File API Test
 * 
 * @author linux_china
 */
public class OpenAIFileAPITest extends ProjectBootBaseTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private OpenAIFileAPI openAIFileAPI;

	@Autowired
	private OpenAIBatchAPI openAIBatchAPI;

	@Test
	public void testUpload() {
		String jsonl = Stream.of("What's Java Language?", "What's Kotlin Language?")
			.map(ChatCompletionRequest::of)
			.map(ChatCompletionBatchRequest::new)
			.map(this::toJson)
			.filter(Strings::isNotBlank)
			.collect(Collectors.joining("\n"));
		Resource resource = new ByteArrayResource(jsonl.getBytes());
		FileObject fileObject = openAIFileAPI.upload("batch", resource).block();
		CreateBatchRequest createBatchRequest = new CreateBatchRequest(fileObject.getId());
		BatchObject batchObject = openAIBatchAPI.create(createBatchRequest).block();
	}

	@Test
	public void testList() {
		final ListFilesResponse listFilesResponse = openAIFileAPI.list().block();
		assertThat(listFilesResponse.getData()).isNotEmpty();
	}

	@Test
	public void testRetrieve() {
		final FileObject fileObject = openAIFileAPI.retrieve("file-hvSA7nhEIHbAfDww72qssk3k").block();
		System.out.println(fileObject.getId());
	}

	private String toJson(ChatCompletionBatchRequest request) {
		try {
			return objectMapper.writeValueAsString(request);
		}
		catch (Exception e) {
			return "";
		}
	}

}
