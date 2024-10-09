package org.mvnsearch.chatgpt.model.file;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * File object
 * 
 * @author linux_china
 */
public class FileObject {

	private String id;

	private String object;

	private long bytes;

	@JsonProperty("created_at")
	private long createdAt;

	private String filename;

	private String purpose;

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

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
}
