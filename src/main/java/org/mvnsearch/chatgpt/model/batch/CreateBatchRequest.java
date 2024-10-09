package org.mvnsearch.chatgpt.model.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * create batch request
 * 
 * @author linux_china
 */
public class CreateBatchRequest {

	@JsonProperty("input_file_id")
	private String inputFileId;

	private String endpoint = "/v1/chat/completions";

	@JsonProperty("completion_window")
	private String completionWindow = "24h";

	public CreateBatchRequest() {
	}

	public CreateBatchRequest(String inputFileId) {
		this.inputFileId = inputFileId;
	}

	public String getInputFileId() {
		return inputFileId;
	}

	public void setInputFileId(String inputFileId) {
		this.inputFileId = inputFileId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getCompletionWindow() {
		return completionWindow;
	}

	public void setCompletionWindow(String completionWindow) {
		this.completionWindow = completionWindow;
	}

}
