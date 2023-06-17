package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.List;


public class ChatCompletionResponse {
    private String id;
    private ChatCompletionUsage usage;
    private List<ChatCompletionChoice> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ChatCompletionUsage getUsage() {
        return usage;
    }

    public void setUsage(ChatCompletionUsage usage) {
        this.usage = usage;
    }

    public List<ChatCompletionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<ChatCompletionChoice> choices) {
        this.choices = choices;
    }

    @JsonIgnore
    public List<ChatMessage> getReply() {
        if (this.choices == null || this.choices.isEmpty()) return Collections.emptyList();
        return this.choices.stream().map(ChatCompletionChoice::getMessage).toList();
    }

    @JsonIgnore
    public String getReplyText() {
        if (this.choices == null || this.choices.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (ChatCompletionChoice choice : choices) {
            final ChatMessage message = choice.getMessage();
            if (message != null && message.getContent() != null) {
                sb.append(message.getContent());
            }
        }
        return sb.toString();
    }
}
