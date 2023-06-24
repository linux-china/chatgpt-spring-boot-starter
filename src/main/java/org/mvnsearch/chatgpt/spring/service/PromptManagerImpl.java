package org.mvnsearch.chatgpt.spring.service;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PromptManagerImpl implements PromptManager {

	private final Map<String, String> allPrompts = new HashMap<>();

	PromptManagerImpl(List<PromptStore> promptStores) throws Exception {
		for (PromptStore promptStore : promptStores) {
			allPrompts.putAll(promptStore.readAll());
		}
	}

	public String prompt(@PropertyKey(resourceBundle = PROMPTS_FQN) String key, Object... params) {
		if (allPrompts.containsKey(key)) {
			String prompt = allPrompts.get(key);
			if (params != null && params.length > 0 && prompt.indexOf('{') >= 0) {
				prompt = MessageFormat.format(prompt, params);
			}
			return prompt;
		}
		else {
			return "!!!" + key + "!!!";
		}
	}

	@Override
	public Map<String, String> getAllPrompts() {
		return allPrompts;
	}

	@Override
	@Nullable
	public String getRawPrompt(String key) {
		return allPrompts.get(key);
	}

	@Override
	public boolean containsPrompt(String key) {
		return allPrompts.containsKey(key);
	}

}
