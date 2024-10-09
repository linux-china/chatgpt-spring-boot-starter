package org.mvnsearch.chatgpt.model.batch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Batch Object
 * 
 * @author linux_china
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchObject {

	private String id;

	private String object;

	private String endpoint;

	private BatchObjectErrors errors;

	@JsonProperty("input_file_id")
	private String inputFileId;

	@JsonProperty("completion_window")
	private String completionWindow;

	private String status;

	@JsonProperty("output_file_id")
	private String outputFileId;

	@JsonProperty("error_file_id")
	private String errorFileId;

	@JsonProperty("created_at")
	private Long createdAt;

	@JsonProperty("in_progress_at")
	private Long inProgressAt;

	@JsonProperty("expires_at")
	private Long expiresAt;

	@JsonProperty("finalizing_at")
	private Long finalizingAt;

	@JsonProperty("completed_at")
	private Long completedAt;

	@JsonProperty("failed_at")
	private Long failedAt;

	@JsonProperty("cancelling_at")
	private Long cancellingAt;

	@JsonProperty("cancelled_at")
	private Long cancelledAt;

	@JsonProperty("request_counts")
	private RequestCounts requestCounts;

	private Map<String, String> metadata;

	public record BatchObjectErrors(String object, List<BatchObjectError> data) {

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record BatchObjectError(String code, String message, String param, Integer line) {
	}

	public record RequestCounts(Long total, Long completed, Integer failed) {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public BatchObjectErrors getErrors() {
		return errors;
	}

	public void setErrors(BatchObjectErrors errors) {
		this.errors = errors;
	}

	public String getInputFileId() {
		return inputFileId;
	}

	public void setInputFileId(String inputFileId) {
		this.inputFileId = inputFileId;
	}

	public String getCompletionWindow() {
		return completionWindow;
	}

	public void setCompletionWindow(String completionWindow) {
		this.completionWindow = completionWindow;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOutputFileId() {
		return outputFileId;
	}

	public void setOutputFileId(String outputFileId) {
		this.outputFileId = outputFileId;
	}

	public String getErrorFileId() {
		return errorFileId;
	}

	public void setErrorFileId(String errorFileId) {
		this.errorFileId = errorFileId;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getInProgressAt() {
		return inProgressAt;
	}

	public void setInProgressAt(Long inProgressAt) {
		this.inProgressAt = inProgressAt;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Long getFinalizingAt() {
		return finalizingAt;
	}

	public void setFinalizingAt(Long finalizingAt) {
		this.finalizingAt = finalizingAt;
	}

	public Long getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Long completedAt) {
		this.completedAt = completedAt;
	}

	public Long getFailedAt() {
		return failedAt;
	}

	public void setFailedAt(Long failedAt) {
		this.failedAt = failedAt;
	}

	public Long getCancellingAt() {
		return cancellingAt;
	}

	public void setCancellingAt(Long cancellingAt) {
		this.cancellingAt = cancellingAt;
	}

	public Long getCancelledAt() {
		return cancelledAt;
	}

	public void setCancelledAt(Long cancelledAt) {
		this.cancelledAt = cancelledAt;
	}

	public RequestCounts getRequestCounts() {
		return requestCounts;
	}

	public void setRequestCounts(RequestCounts requestCounts) {
		this.requestCounts = requestCounts;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

}
