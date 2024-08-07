package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.mvnsearch.chatgpt.model.function.Parameters;

/**
 * Response format json_schema
 * 
 * @author linux_china
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseFormatJsonSchema {

	private String name;

	private String description;

	private Parameters schema;

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

	public Parameters getSchema() {
		return schema;
	}

	public void setSchema(Parameters schema) {
		this.schema = schema;
	}

}
