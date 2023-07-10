package org.mvnsearch.chatgpt.model.embedding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmbeddingsResponse {

	private String model;

	private String object;

	private EmbeddingsUsage usage;

	private List<EmbeddingsResult> data;

	public record EmbeddingsUsage(@JsonProperty("prompt_tokens") Integer promptTokens,
			@JsonProperty("total_tokens") Integer totalTokens) {
	}

	public record EmbeddingsResult(String object, Integer index, List<Float> embedding) {
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public EmbeddingsUsage getUsage() {
		return usage;
	}

	public void setUsage(EmbeddingsUsage usage) {
		this.usage = usage;
	}

	public List<EmbeddingsResult> getData() {
		return data;
	}

	public void setData(List<EmbeddingsResult> data) {
		this.data = data;
	}

	@JsonIgnore
	public List<Float> getEmbeddings() {
		if (this.data != null && !this.data.isEmpty()) {
			List<Float> embeddings = new ArrayList<>();
			for (EmbeddingsResult embeddingsResult : data) {
				embeddings.addAll(embeddingsResult.embedding);
			}
			return embeddings;
		}
		return Collections.emptyList();
	}

}
