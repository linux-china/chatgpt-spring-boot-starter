package org.mvnsearch.chatgpt.model.completion.chat;


public class ChatTool {
    private String type = "function";
    private ChatFunction function;

    public ChatTool() {
    }

    public ChatTool(ChatFunction function) {
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChatFunction getFunction() {
        return function;
    }

    public void setFunction(ChatFunction function) {
        this.function = function;
    }
}
