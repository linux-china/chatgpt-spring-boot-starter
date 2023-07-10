package org.mvnsearch.chatgpt.model.completion.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * completion request for <a href="https://api.openai.com/v1/completions">...</a>
 *
 * @author linux_china
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletionRequest {

	private String model;

	private String prompt;

	private String suffix;

	private Double temperature;

	private Integer n;

	@JsonProperty("top_p")
	private Double topP;

	private Boolean stream;

	private List<String> stop;

	@JsonProperty("max_tokens")
	private Integer maxTokens;

	private Integer logprobs;

	private Boolean echo;

	@JsonProperty("presence_penalty")
	private Double presencePenalty;

	@JsonProperty("frequency_penalty")
	private Double frequencyPenalty;

	@JsonProperty("best_of")
	private Integer bestOf;

	@JsonProperty("logit_bias")
	private Map<String, Integer> logitBias;

	private String user;

	public CompletionRequest() {
	}

	public CompletionRequest(String model, String prompt) {
		this.model = model;
		this.prompt = prompt;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Integer getN() {
		return n;
	}

	public void setN(Integer n) {
		this.n = n;
	}

	public Double getTopP() {
		return topP;
	}

	public void setTopP(Double topP) {
		this.topP = topP;
	}

	public Boolean getStream() {
		return stream;
	}

	public void setStream(Boolean stream) {
		this.stream = stream;
	}

	public List<String> getStop() {
		return stop;
	}

	public void setStop(List<String> stop) {
		this.stop = stop;
	}

	public Integer getMaxTokens() {
		return maxTokens;
	}

	public void setMaxTokens(Integer maxTokens) {
		this.maxTokens = maxTokens;
	}

	public Integer getLogprobs() {
		return logprobs;
	}

	public void setLogprobs(Integer logprobs) {
		this.logprobs = logprobs;
	}

	public Boolean getEcho() {
		return echo;
	}

	public void setEcho(Boolean echo) {
		this.echo = echo;
	}

	public Double getPresencePenalty() {
		return presencePenalty;
	}

	public void setPresencePenalty(Double presencePenalty) {
		this.presencePenalty = presencePenalty;
	}

	public Double getFrequencyPenalty() {
		return frequencyPenalty;
	}

	public void setFrequencyPenalty(Double frequencyPenalty) {
		this.frequencyPenalty = frequencyPenalty;
	}

	public Integer getBestOf() {
		return bestOf;
	}

	public void setBestOf(Integer bestOf) {
		this.bestOf = bestOf;
	}

	public Map<String, Integer> getLogitBias() {
		return logitBias;
	}

	public void setLogitBias(Map<String, Integer> logitBias) {
		this.logitBias = logitBias;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
