package org.mvnsearch.chatgpt.model.completion.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * response_format for structured output
 * 
 * @author linux_china
 */
public class ResponseFormat {

	private String type = "json_schema";

	@JsonProperty("json_schema")
	private ResponseFormatJsonSchema jsonSchema;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ResponseFormatJsonSchema getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(ResponseFormatJsonSchema jsonSchema) {
		this.jsonSchema = jsonSchema;
	}

	public static ResponseFormat jsonSchema(ResponseFormatJsonSchema jsonSchema) {
		ResponseFormat responseFormat = new ResponseFormat();
		responseFormat.setJsonSchema(jsonSchema);
		return responseFormat;
	}

}
