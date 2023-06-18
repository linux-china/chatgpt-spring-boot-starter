package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatFunctionParameters {
    private String type;
    Map<String, Object> properties;
    List<String> required;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }
}
