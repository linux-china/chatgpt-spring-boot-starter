package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.mvnsearch.chatgpt.model.function.Parameters;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatFunction {

	private String name;

	private String description;

	private Parameters parameters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

}
