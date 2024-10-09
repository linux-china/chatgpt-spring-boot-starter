package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.uuid.Generators;

/**
 * Batch API request to generate .jsonl file
 *
 * @author linux_china
 */
public class ChatCompletionBatchRequest {

	@JsonProperty("custom_id")
	private String customId;

	private String method = "POST";

	private String url = "/v1/chat/completions";

	private ChatCompletionRequest body;

	public ChatCompletionBatchRequest() {
	}

	public ChatCompletionBatchRequest(ChatCompletionRequest body) {
		this.customId = Generators.timeBasedEpochGenerator().generate().toString();
		this.body = body;
	}

	public ChatCompletionBatchRequest(String customId, ChatCompletionRequest body) {
		this.customId = customId;
		this.body = body;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ChatCompletionRequest getBody() {
		return body;
	}

	public void setBody(ChatCompletionRequest body) {
		this.body = body;
	}

}
