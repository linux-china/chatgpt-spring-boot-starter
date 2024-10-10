package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Test;
import org.mvnsearch.chatgpt.demo.ProjectBootBaseTest;
import org.mvnsearch.chatgpt.model.batch.BatchObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * OpenAI Batch API Test
 * 
 * @author linux_china
 */
public class OpenAIBatchAPITest extends ProjectBootBaseTest {

	@Autowired
	private OpenAIBatchAPI openAIBatchAPI;

	@Test
	public void testBatchStatus() {
		final BatchObject batchObject = openAIBatchAPI.retrieve("batch_6706800879508190bb30b4161f38f283").block();
		System.out.println(batchObject.getStatus());
	}

}
