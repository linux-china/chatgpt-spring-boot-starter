package org.mvnsearch.chatgpt.spring.service;

import java.io.IOException;
import java.util.Map;

/**
 * prompts store interface
 *
 * @author linux_china
 */
public interface PromptStore {

	Map<String, String> readAll() throws IOException;

}
