package org.mvnsearch.chatgpt.model;

public enum ChatMessageRole {
    system("system"),

    user("user"),

    assistant("assistant");

    private final String name;

    ChatMessageRole(final String name) {
        this.name = name;
    }

    public String value() {
        return name;
    }
}
