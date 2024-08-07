package org.mvnsearch.chatgpt.model.completion.chat;

import org.mvnsearch.chatgpt.model.function.Parameters;

/**
 * Response format json_schema
 * 
 * @author linux_china
 */
public class ResponseFormatJsonSchema {
    private  String name;
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
