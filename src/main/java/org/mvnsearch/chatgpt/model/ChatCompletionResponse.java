package org.mvnsearch.chatgpt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


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

    @JsonIgnore
    public Mono<String> getReplyCombinedText() {
        if (this.choices == null || this.choices.isEmpty()) return Mono.empty();
        return Flux.fromIterable(choices).flatMap(choice -> {
                    final ChatMessage message = choice.getMessage();
                    if (message != null) {
                        if (message.getContent() != null) {
                            return Mono.just(message.getContent());
                        }
                        final FunctionCall functionCall = message.getFunctionCall();
                        if (functionCall != null && functionCall.getFunctionStub() != null) {
                            try {
                                final Object result = functionCall.getFunctionStub().call();
                                if (result != null) {
                                    if (result instanceof Mono) {
                                        return (Mono<?>) result;
                                    } else {
                                        return Mono.justOrEmpty(result);
                                    }
                                }
                            } catch (Exception e) {
                                return Mono.error(e);
                            }
                        }
                    }
                    return Mono.empty();
                }).collectList()
                .map(items -> items.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining()));
    }
}
