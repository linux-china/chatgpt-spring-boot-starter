package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.file.DeleteFileResponse;
import org.mvnsearch.chatgpt.model.file.FileObject;
import org.mvnsearch.chatgpt.model.file.ListFilesResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

/**
 * OpenAI File API
 *
 * @see <a href="https://platform.openai.com/docs/api-reference/files">OpenAI File API</a>
 * @author linux_china
 */
public interface OpenAIFileAPI {

	@PostExchange("/files")
	Mono<FileObject> upload(@RequestPart("purpose") String purpose, @RequestPart("file") Resource file);

	@GetExchange("/files")
	Mono<ListFilesResponse> list();

	@GetExchange("/files/{file_id}")
	Mono<FileObject> retrieve(@PathVariable("file_id") String fileId);

	@DeleteExchange("/files/{file_id}")
	Mono<DeleteFileResponse> delete(@PathVariable("file_id") String fileId);

	@GetExchange("/files/{file_id}/content")
	Mono<byte[]> content(@PathVariable("file_id") String fileId);

}
