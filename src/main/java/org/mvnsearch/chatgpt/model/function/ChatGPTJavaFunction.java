package org.mvnsearch.chatgpt.model.function;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.mvnsearch.chatgpt.model.ChatFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGPTJavaFunction {

	private String name;

	private String description;

	private ChatFunction.Parameters parameters;

	@JsonIgnore
	private Method javaMethod;

	@JsonIgnore
	private Class<?> parameterType;

	@JsonIgnore
	private Object target;

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

	public ChatFunction.Parameters getParameters() {
		return parameters;
	}

	public void setParameters(ChatFunction.Parameters parameters) {
		this.parameters = parameters;
	}

	public Method getJavaMethod() {
		return javaMethod;
	}

	public void setJavaMethod(Method javaMethod) {
		this.javaMethod = javaMethod;
	}

	public Class<?> getParameterType() {
		return parameterType;
	}

	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public void addProperty(String name, String type, String description) {
		if (this.parameters == null) {
			this.parameters = new ChatFunction.Parameters("object", new HashMap<>(), new ArrayList<>());
		}
		this.parameters.getProperties().put(name, new ChatFunction.JsonSchemaProperty(name, type, description));
	}

	public void addArrayProperty(String name, String type, String description) {
		if (this.parameters == null) {
			this.parameters = new ChatFunction.Parameters("object", new HashMap<>(), new ArrayList<>());
		}
		this.parameters.getProperties()
			.put(name, new ChatFunction.JsonSchemaProperty(name, "array", description,
					new ChatFunction.JsonArrayItems(type, null)));
	}

	public void addRequired(String name) {
		this.getParameters().getRequired().add(name);
	}

	public ChatFunction toChatFunction() {
		ChatFunction chatFunction = new ChatFunction();
		chatFunction.setName(this.name);
		chatFunction.setDescription(this.description);
		chatFunction.setParameters(this.parameters);
		return chatFunction;
	}

	public Object call(String argumentsJson) throws Exception {
		return GPTFunctionUtils.callGPTFunction(target, this, argumentsJson);
	}

}
