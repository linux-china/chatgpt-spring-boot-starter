package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.Callable;

public class FunctionCall {

	private String name;

	private String arguments;

	@JsonIgnore
	private Callable<Object> functionStub;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}

	public Callable<Object> getFunctionStub() {
		return functionStub;
	}

	public void setFunctionStub(Callable<Object> functionStub) {
		this.functionStub = functionStub;
	}

}
