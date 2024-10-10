package org.mvnsearch.chatgpt.model.file;

import java.util.List;

/**
 * List files response
 *
 * @author linux_china
 */
public class ListFilesResponse {

	private List<FileObject> data;

	private String object;

	public List<FileObject> getData() {
		return data;
	}

	public void setData(List<FileObject> data) {
		this.data = data;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
