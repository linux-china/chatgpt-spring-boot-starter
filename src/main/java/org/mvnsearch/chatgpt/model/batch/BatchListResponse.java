package org.mvnsearch.chatgpt.model.batch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Batch List response
 * 
 * @author linux_china
 */
public class BatchListResponse {

	private String object;

	private List<BatchObject> data;

	@JsonProperty("first_id")
	private String firstId;

	@JsonProperty("last_id")
	private String lastId;

	@JsonProperty("has_more")
	private boolean hasMore;

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public List<BatchObject> getData() {
		return data;
	}

	public void setData(List<BatchObject> data) {
		this.data = data;
	}

	public String getFirstId() {
		return firstId;
	}

	public void setFirstId(String firstId) {
		this.firstId = firstId;
	}

	public String getLastId() {
		return lastId;
	}

	public void setLastId(String lastId) {
		this.lastId = lastId;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

}
