package org.mvnsearch.chatgpt.spring.service;

import org.mvnsearch.chatgpt.model.ChatFunction;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository for all GPT callback functions.
 */
class GPTFunctionRegistry
		implements BeanRegistrationAotProcessor, BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {

	private static final Logger log = LoggerFactory.getLogger(GPTFunctionRegistry.class);

	private final BindingReflectionHintsRegistrar reflectionRegistrar = new BindingReflectionHintsRegistrar();

	private final Map<String, ChatGPTJavaFunction> allJsonSchemaFunctions = new ConcurrentHashMap<>();

	private final Map<String, ChatFunction> allChatFunctions = new ConcurrentHashMap<>();

	public ChatFunction getChatFunction(String functionName) {
		return this.allChatFunctions.get(functionName);
	}

	public ChatGPTJavaFunction getJsonSchemaFunction(String functionName) {
		return this.allJsonSchemaFunctions.get(functionName);
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("ChatGPTService initialized with {} functions", this.allJsonSchemaFunctions.size());
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		try {
			Map<String, ChatGPTJavaFunction> functions = GPTFunctionUtils.extractFunctions(bean.getClass());
			if (!functions.isEmpty()) {
				log.info("found {} functions on bean name {} with class {}", functions.size(), beanName,
						bean.getClass().getName());
				for (Map.Entry<String, ChatGPTJavaFunction> entry : functions.entrySet()) {
					ChatGPTJavaFunction jsonSchemaFunction = entry.getValue();
					jsonSchemaFunction.setTarget(bean);
					this.allJsonSchemaFunctions.put(entry.getKey(), jsonSchemaFunction);
					this.allChatFunctions.put(entry.getKey(), jsonSchemaFunction.toChatFunction());
				}
			}
		} //
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		return bean;
	}

	static class GPTFunctionBeanRegistrationAotContribution implements BeanRegistrationAotContribution {

		private final Map<String, ChatGPTJavaFunction> functions;

		public GPTFunctionBeanRegistrationAotContribution(Map<String, ChatGPTJavaFunction> functions) {
			this.functions = functions;
		}

		@Override
		public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {

		}

	}

	@Override
	public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
		Class<?> beanClass = registeredBean.getBeanClass();
		try {
			Map<String, ChatGPTJavaFunction> functions = GPTFunctionUtils.extractFunctions(beanClass);
			if (!functions.isEmpty()) {

				functions.forEach((functionName, function) -> log.info(
						"Registering hints for function {} on bean {} with class {}", functionName,
						registeredBean.getBeanName(), registeredBean.getBeanClass().getName()));

				return (generationContext, beanRegistrationCode) -> {
					ReflectionHints reflection = generationContext.getRuntimeHints().reflection();
					reflection.registerType(beanClass, MemberCategory.values());
					this.reflectionRegistrar.registerReflectionHints(reflection, beanClass);

					for (ChatGPTJavaFunction function : functions.values()) {
						Method method = function.getJavaMethod();
						MemberCategory[] mcs = MemberCategory.values();
						reflection.registerType(method.getReturnType(), mcs);
						for (Class<?> pt : method.getParameterTypes()) {
							reflection.registerType(pt, mcs);
						}

					}

					/*
					 * ReflectionUtils.doWithMethods(beanClass, method ->
					 * Stream.of(method.getParameters()) .forEach(param ->
					 * reflection.registerType(param.getType(),
					 * MemberCategory.values())));
					 */
				};
			}
		} //
		catch (Exception e) {
			throw new RuntimeException(
					String.format("couldn't read the functions on bean class %s", beanClass.getName()), e);
		}

		return null;
	}

	@Override
	public boolean isBeanExcludedFromAotProcessing() {
		return false;
	}

}
