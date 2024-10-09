package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.batch.BatchListResponse;
import org.mvnsearch.chatgpt.model.batch.BatchObject;
import org.mvnsearch.chatgpt.model.batch.CreateBatchRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

/**
 * OpenAI Batch API
 * 
 * @author linux_china
 */
public interface OpenAIBatchAPI {

	@PostExchange("/batches")
	Mono<BatchObject> create(CreateBatchRequest request);

	@GetExchange("/batches")
	Mono<BatchListResponse> list();

	@GetExchange("/batches/{batch_id}")
	Mono<BatchObject> retrieve(@PathVariable("batch_id") String batchId);

	@PostExchange("/batches/{batch_id}/cancel")
	Mono<BatchObject> cancel(@PathVariable("batch_id") String batchId);

}
