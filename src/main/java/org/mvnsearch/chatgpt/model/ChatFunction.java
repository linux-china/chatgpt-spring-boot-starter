package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

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

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class Parameters {

		private String type;

		private Map<String, JsonSchemaProperty> properties;

		private List<String> required;

		public Parameters() {
		}

		public Parameters(String type, Map<String, JsonSchemaProperty> properties, List<String> required) {
			this.type = type;
			this.properties = properties;
			this.required = required;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Map<String, JsonSchemaProperty> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, JsonSchemaProperty> properties) {
			this.properties = properties;
		}

		public List<String> getRequired() {
			return required;
		}

		public void setRequired(List<String> required) {
			this.required = required;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class JsonSchemaProperty {

		@JsonIgnore
		private String name;

		private String type;

		private String description;

		/**
		 * items for type array
		 */
		private JsonArrayItems items;

		public JsonSchemaProperty() {
		}

		public JsonSchemaProperty(String name, String type, String description) {
			this.name = name;
			this.type = type;
			this.description = description;
		}

		public JsonSchemaProperty(String name, String type, String description, JsonArrayItems items) {
			this.name = name;
			this.type = type;
			this.description = description;
			this.items = items;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public JsonArrayItems getItems() {
			return items;
		}

		public void setItems(JsonArrayItems items) {
			this.items = items;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class JsonArrayItems {

		private String type;

		private String description;

		public JsonArrayItems() {
		}

		public JsonArrayItems(String type, String description) {
			this.type = type;
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

}
