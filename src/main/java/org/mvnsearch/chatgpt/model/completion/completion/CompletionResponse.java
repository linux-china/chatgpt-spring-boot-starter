package org.mvnsearch.chatgpt.model.completion.completion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mvnsearch.chatgpt.model.CompletionUsage;

import java.util.List;

public class CompletionResponse {

	private String id;

	private String model;

	private Long created;

	private String object;

	private List<CompletionMessage> choices;

	private CompletionUsage usage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public List<CompletionMessage> getChoices() {
		return choices;
	}

	public void setChoices(List<CompletionMessage> choices) {
		this.choices = choices;
	}

	public CompletionUsage getUsage() {
		return usage;
	}

	public void setUsage(CompletionUsage usage) {
		this.usage = usage;
	}

	@JsonIgnore
	public String getReplyText() {
		if (choices != null && !choices.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (CompletionMessage message : choices) {
				builder.append(message.getText());
			}
			return builder.toString();
		}
		return "";
	}

}
