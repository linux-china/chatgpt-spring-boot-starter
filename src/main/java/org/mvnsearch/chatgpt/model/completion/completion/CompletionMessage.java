package org.mvnsearch.chatgpt.model.completion.completion;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompletionMessage {

	private Integer index;

	private Integer logprobs;

	private String text;

	@JsonProperty("finish_reason")
	private String finishReason;

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getLogprobs() {
		return logprobs;
	}

	public void setLogprobs(Integer logprobs) {
		this.logprobs = logprobs;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getFinishReason() {
		return finishReason;
	}

	public void setFinishReason(String finishReason) {
		this.finishReason = finishReason;
	}

}
