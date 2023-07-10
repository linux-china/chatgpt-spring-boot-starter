package org.mvnsearch.chatgpt.model.embedding;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EmbeddingsRequest(String model, String input, String user) {

	public static EmbeddingsRequest of(String text) {
		return new EmbeddingsRequest("text-embedding-ada-002", text, null);
	}
}
