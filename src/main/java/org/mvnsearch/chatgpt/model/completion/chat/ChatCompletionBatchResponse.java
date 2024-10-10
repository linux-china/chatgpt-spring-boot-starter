package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * chat completion batch response
 * 
 * @author linux_china
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionBatchResponse {

	private String id;

	@JsonProperty("custom_id")
	private String customId;

	private ChatCompletionBatchHttpResponse response;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ChatCompletionBatchHttpResponse(@JsonProperty("status_code") int statusCode,
			@JsonProperty("request_id") String requestId, ChatCompletionResponse body) {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public ChatCompletionBatchHttpResponse getResponse() {
		return response;
	}

	public void setResponse(ChatCompletionBatchHttpResponse response) {
		this.response = response;
	}

}
