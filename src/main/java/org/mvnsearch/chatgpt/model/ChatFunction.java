package org.mvnsearch.chatgpt.model;

public class ChatFunction {
    private String name;
    private String description;
    private ChatFunctionParameters parameters;

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

    public ChatFunctionParameters getParameters() {
        return parameters;
    }

    public void setParameters(ChatFunctionParameters parameters) {
        this.parameters = parameters;
    }
}
