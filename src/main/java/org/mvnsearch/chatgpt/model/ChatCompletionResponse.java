package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.List;


public class ChatCompletionResponse {
    private String id;
    private String model;
    private Long created;
    private String object;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @JsonIgnore
    public List<ChatMessage> getReply() {
        if (this.choices == null || this.choices.isEmpty()) return Collections.emptyList();
        return this.choices.stream().map(ChatCompletionChoice::getMessage).toList();
    }

    /**
     * get reply text from messages' content
     *
     * @return reply text
     */
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

    /**
     * get reply combined text with function call result embedded
     *
     * @return reply text
     */
    @JsonIgnore
    public String getReplyCombinedText() {
        if (this.choices == null || this.choices.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (ChatCompletionChoice choice : choices) {
            final ChatMessage message = choice.getMessage();
            if (message != null) {
                if (message.getContent() != null) {
                    sb.append(message.getContent());
                }
                final FunctionCall functionCall = message.getFunctionCall();
                if (functionCall != null) {
                    try {
                        final Object result = functionCall.getFunctionStub().call();
                        if (result != null) {
                            sb.append(result);
                        }
                    } catch (Exception e) {
                        sb.append("FunctionError: ").append(e.getMessage());
                    }
                }
            }
        }
        return sb.toString();
    }
}
