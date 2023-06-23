package org.mvnsearch.chatgpt.spring.service.impl;

import org.mvnsearch.chatgpt.model.ChatFunction;
import org.mvnsearch.chatgpt.model.function.ChatGPTJavaFunction;
import org.mvnsearch.chatgpt.model.function.GPTFunctionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Repository for all GPT callback functions.
 */
class GPTFunctionRegistry implements BeanRegistrationAotProcessor, BeanPostProcessor, ApplicationListener<ApplicationReadyEvent> {


    private static final Logger log = LoggerFactory.getLogger(GPTFunctionRegistry.class);

    private final BindingReflectionHintsRegistrar reflectionRegistrar = new BindingReflectionHintsRegistrar();

    private final Map<String, ChatGPTJavaFunction> allJsonSchemaFunctions = new HashMap<>();

    private final Map<String, ChatFunction> allChatFunctions = new HashMap<>();


    public ChatFunction getChatFunction(String functionName) {
        return this.allChatFunctions.get(functionName);
    }

    public ChatGPTJavaFunction getJsonSchemaFunction(String functionName) {
        return this.allJsonSchemaFunctions.get(functionName);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("ChatGPTService initialized with {} functions", allJsonSchemaFunctions.size());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            final Map<String, ChatGPTJavaFunction> functions = GPTFunctionUtils.extractFunctions(bean.getClass());
            if (!functions.isEmpty()) {
                log.info("found [" + functions.size() + "] functions on bean name [" + beanName + "] with class [" +
                         bean.getClass().getName() + "]");
                for (Map.Entry<String, ChatGPTJavaFunction> entry : functions.entrySet()) {
                    final ChatGPTJavaFunction jsonSchemaFunction = entry.getValue();
                    jsonSchemaFunction.setTarget(bean);
                    this.allJsonSchemaFunctions.put(entry.getKey(), jsonSchemaFunction);
                    this.allChatFunctions.put(entry.getKey(), jsonSchemaFunction.toChatFunction());
                }
            }
        }//
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bean;
    }


    @Override
    public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        final Class<?> beanClass = registeredBean.getBeanClass();
        try {
            final Map<String, ChatGPTJavaFunction> functions = GPTFunctionUtils.extractFunctions(beanClass);
            functions.forEach((functionName, function) -> log.info("Registering hints for function name [" + functionName + "] on bean [" + registeredBean.getBeanName() + "] with class [" + beanClass.getName() + "]"));
            if (!functions.isEmpty()) {
                return (generationContext, beanRegistrationCode) -> {
                    final ReflectionHints reflection = generationContext.getRuntimeHints().reflection();
                    reflection.registerType(beanClass, MemberCategory.values());
                    this.reflectionRegistrar.registerReflectionHints(reflection, beanClass);
                    ReflectionUtils.doWithMethods(beanClass, method -> Stream.of(method.getParameters()).forEach(param -> reflection.registerType(param.getType(), MemberCategory.values())));
                };
            }
        }//
        catch (Exception e) {
            throw new RuntimeException("couldn't read the functions on bean class [" + beanClass.getName() + "]");
        }

        return null;
    }

    @Override
    public boolean isBeanExcludedFromAotProcessing() {
        return false;
    }


}


abstract class TypeCrawler {

    public static Set<Type> crawl(Type t) {
        Set<Type> seen = new HashSet<>();
        crawl(t, seen);
        return seen;
    }

    private static void crawl(Type type, Set<Type> seen) {
        if (seen.contains(type)) {
            return;
        }
        seen.add(type);
        if (type instanceof Class<?> clazz) {


            for (Constructor c : clazz.getDeclaredConstructors()) {

                for (Type t : c.getParameterTypes()) {
                    crawl(t, seen);
                }
            }

            if (clazz.getRecordComponents() != null) {
                for (RecordComponent component : clazz.getRecordComponents()) {
                    crawl(component.getType(), seen);
                }
            }
            for (Method m : clazz.getDeclaredMethods()) {
                crawl(m.getReturnType(), seen);
                crawl(m.getGenericReturnType(), seen);
                for (Class<?> c : m.getParameterTypes()) {
                    crawl(c, seen);
                }
            }
        }//

    }
}