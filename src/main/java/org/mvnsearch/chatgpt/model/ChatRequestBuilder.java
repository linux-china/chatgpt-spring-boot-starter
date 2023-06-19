package org.mvnsearch.chatgpt.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRequestBuilder {
    private final ChatCompletionRequest request = new ChatCompletionRequest();

    /**
     * construct ChatGPT request builder from user message
     *
     * @param userMessage user message content
     * @return builder
     */
    public static ChatRequestBuilder of(String userMessage) {
        return new ChatRequestBuilder().userMessage(userMessage);
    }

    /**
     * construct ChatGPT request builder from system and user messages
     *
     * @param systemMessage system message content
     * @param userMessage   user message content
     * @return builder
     */
    public static ChatRequestBuilder of(String systemMessage, String userMessage) {
        return new ChatRequestBuilder().systemMessage(systemMessage).userMessage(userMessage);
    }

    public ChatRequestBuilder() {
    }

    /**
     * ID of the model to use
     *
     * @param model model ID, Defaults to gpt-3.5-turbo
     * @return builder
     */
    public ChatRequestBuilder model(String model) {
        request.setModel(model);
        return this;
    }

    /**
     * What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic.
     *
     * @param temperature sampling temperature, Defaults to 1
     * @return builder
     */
    public ChatRequestBuilder temperature(Double temperature) {
        request.setTemperature(temperature);
        return this;
    }

    /**
     * How many chat completion choices to generate for each input message.
     *
     * @param n choices number, Defaults to 1
     * @return builder
     */
    public ChatRequestBuilder n(Integer n) {
        request.setN(n);
        return this;
    }

    /**
     * The maximum number of tokens to generate in the chat completion.
     *
     * @param maxTokens maximum number of tokens
     * @return builder
     */
    public ChatRequestBuilder maxTokens(Integer maxTokens) {
        request.setMaxTokens(maxTokens);
        return this;
    }

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     *
     * @param topP top_p, default is 1
     * @return builder
     */
    public ChatRequestBuilder topP(Double topP) {
        request.setTopP(topP);
        return this;
    }

    /**
     * If set, partial message deltas will be sent, like in ChatGPT. Tokens will be sent as data-only server-sent events as they become available, with the stream terminated by a data: [DONE] message.
     *
     * @param stream stream or not, Defaults to false
     * @return builder
     */
    public ChatRequestBuilder stream(Boolean stream) {
        request.setStream(stream);
        return this;
    }

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     *
     * @param stop stop sequence
     * @return builder
     */
    public ChatRequestBuilder stop(String stop) {
        if (request.getStop() == null) {
            request.setStop(new ArrayList<>());
        }
        request.getStop().add(stop);
        return this;
    }

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     *
     * @param stop stop sequences
     * @return builder
     */
    public ChatRequestBuilder stop(List<String> stop) {
        request.setStop(stop);
        return this;
    }

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model's likelihood to talk about new topics.
     *
     * @param presencePenalty presence penalty, default is 0
     * @return builder
     */
    public ChatRequestBuilder presencePenalty(Double presencePenalty) {
        request.setPresencePenalty(presencePenalty);
        return this;
    }

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim.
     *
     * @param frequencyPenalty frequency penalty, default is 0
     * @return builder
     */
    public ChatRequestBuilder frequencyPenalty(Double frequencyPenalty) {
        request.setFrequencyPenalty(frequencyPenalty);
        return this;
    }

    /**
     * Modify the likelihood of specified tokens appearing in the completion.
     *
     * @param name name
     * @param bias bias value from -100 to 100
     * @return builder
     */
    public ChatRequestBuilder logitBias(String name, Integer bias) {
        if (request.getLogitBias() == null) {
            request.setLogitBias(new HashMap<>());
        }
        request.getLogitBias().put(name, bias);
        return this;
    }

    /**
     * associated bias value from -100 to 100
     *
     * @param logitBias logit bias
     * @return builder
     */
    public ChatRequestBuilder logitBias(Map<String, Integer> logitBias) {
        request.setLogitBias(logitBias);
        return this;
    }

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     *
     * @param userId user unique identifier
     * @return builder
     */
    public ChatRequestBuilder endUser(String userId) {
        request.setUser(userId);
        return this;
    }

    /**
     * system message comprising the conversation
     *
     * @param content system message content
     * @return builder
     */
    public ChatRequestBuilder systemMessage(String content) {
        request.addMessage(ChatMessage.assistantMessage(content));
        return this;
    }

    /**
     * user message comprising the conversation
     *
     * @param content user message content
     * @return builder
     */
    public ChatRequestBuilder userMessage(String content) {
        request.addMessage(ChatMessage.userMessage(content));
        return this;
    }

    /**
     * assistant message comprising the conversation
     *
     * @param content assistant message content
     * @return builder
     */
    public ChatRequestBuilder assistantMessage(String content) {
        request.addMessage(ChatMessage.assistantMessage(content));
        return this;
    }

    /**
     * A list of messages comprising the conversation
     *
     * @param messages messages
     * @return builder
     */
    public ChatRequestBuilder messages(List<ChatMessage> messages) {
        request.setMessages(messages);
        return this;
    }

    /**
     * functions the model
     *
     * @param functionName The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum length of 64.
     * @return builder
     */
    public ChatRequestBuilder function(String functionName) {
        request.addFunction(functionName);
        return this;
    }

    /**
     * functions the model
     *
     * @param function chat function
     * @return builder
     */
    public ChatRequestBuilder function(ChatFunction function) {
        request.addFunction(function);
        return this;
    }

    /**
     * generate  the chat completion request
     *
     * @return ChatCompletionRequest
     */
    public ChatCompletionRequest build() {
        return request;
    }

}
