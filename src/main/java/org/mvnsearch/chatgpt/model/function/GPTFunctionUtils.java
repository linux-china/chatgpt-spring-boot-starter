package org.mvnsearch.chatgpt.model.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class GPTFunctionUtils {

	private static final Logger log = LoggerFactory.getLogger(GPTFunctionUtils.class);

	public static final ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
		.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

	/**
	 * extract GPT functions from class by scanning methods with @GPTFunction
	 * @param clazz Java Class
	 * @return GPT functions
	 */
	public static Map<String, ChatGPTJavaFunction> extractFunctions(Class<?> clazz) throws Exception {
		Map<String, ChatGPTJavaFunction> functionDeclares = new HashMap<>();
		for (Method method : clazz.getDeclaredMethods()) {// support non-public callback
															// functions
			// check GPT function or not
			final GPTFunction gptFunctionAnnotation = method.getAnnotation(GPTFunction.class);
			if (gptFunctionAnnotation != null) {
				String functionName = gptFunctionAnnotation.name();
				if (functionName.isEmpty()) {
					functionName = method.getName();
				}
				ChatGPTJavaFunction gptJavaFunction = new ChatGPTJavaFunction();
				gptJavaFunction.setJavaMethod(method);
				gptJavaFunction.setName(functionName);
				gptJavaFunction.setDescription(gptFunctionAnnotation.value());
				final Class<?> requestClazz = method.getParameterTypes()[0];
				gptJavaFunction.setParameterType(requestClazz);
				for (Field field : requestClazz.getDeclaredFields()) {
					// parse properties
					final Parameter functionParamAnnotation = field.getAnnotation(Parameter.class);
					if (functionParamAnnotation != null) {
						String fieldName = functionParamAnnotation.name();
						String fieldType = getJsonSchemaType(field.getType());
						if (fieldName.isEmpty()) {
							fieldName = field.getName();
						}
						if (fieldType.equals("array")) {
							Class<?> actualClazz = parseArrayItemClass(field.getGenericType());
							gptJavaFunction.addArrayProperty(fieldName, getJsonSchemaType(actualClazz),
									functionParamAnnotation.value());
						}
						else if (fieldType.equals("object")) {
							throw new Exception(
									"Object type not supported: " + clazz.getName() + "." + field.getName());
						}
						else {
							gptJavaFunction.addProperty(fieldName, fieldType, functionParamAnnotation.value());
						}
						if (functionParamAnnotation.required()) {
							gptJavaFunction.addRequired(fieldName);
						}
						else {
							for (Annotation annotation : field.getAnnotations()) {
								final String annotationName = annotation.annotationType().getName().toLowerCase();
								if (annotationName.endsWith("nonnull")) {
									gptJavaFunction.addRequired(fieldName);
									break;
								}
							}
						}
					}
				}
				functionDeclares.put(functionName, gptJavaFunction);
			}
		}
		return functionDeclares;
	}

	private static String getJsonSchemaType(Class<?> clazz) {
		if (clazz.equals(Integer.class) || clazz.equals(int.class) || clazz.equals(Long.class)
				|| clazz.equals(long.class)) {
			return "integer";
		}
		else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
			return "boolean";
		}
		else if (clazz.equals(Double.class) || clazz.equals(double.class) || clazz.equals(Float.class)
				|| clazz.equals(float.class)) {
			return "number";
		}
		else if (clazz.equals(String.class)) {
			return "string";
		}
		else if (clazz.equals(List.class)) {
			return "array";
		}
		else {
			return "object";
		}
	}

	/**
	 * call GPT function
	 * @param target target object
	 * @param function json schema function
	 * @param argumentsJson arguments json string
	 * @return result
	 * @throws Exception exception
	 */
	public static Object callGPTFunction(Object target, ChatGPTJavaFunction function, String argumentsJson)
			throws Exception {
		log.info("attempting to call GPTFunction on target [" + target.getClass().getName() + "] with arguments ["
				+ argumentsJson + "]");
		final Method javaMethod = function.getJavaMethod();
		ReflectionUtils.makeAccessible(javaMethod);
		final Class<?> parameterType = function.getParameterType();
		final Object param = objectMapper.readValue(argumentsJson, parameterType);
		return javaMethod.invoke(target, param);
	}

	/**
	 * convert object to text plain to display
	 * @param object object
	 * @return object's text
	 */
	public static String toTextPlain(@Nullable Object object) {
		if (object != null) {
			if (object instanceof String) {
				return (String) object;
			}
			else {
				try {
					final String jsonText = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
					if (jsonText.startsWith("\"") && jsonText.endsWith("\"")) {
						return jsonText.substring(1, jsonText.length() - 1);
					}
					return jsonText;
				}
				catch (Exception ignore) {

				}
			}
		}
		return "";
	}

	/**
	 * GraalVM native image compatible
	 * @param genericType generic type
	 * @return item class
	 */
	private static Class<?> parseArrayItemClass(Type genericType) {
		String itemClassName = genericType.getTypeName();
		if (itemClassName.contains("<")) {
			itemClassName = itemClassName.substring(itemClassName.indexOf("<") + 1, itemClassName.indexOf(">"));
		}
		return switch (itemClassName) {
			case "java.lang.Integer" -> Integer.class;
			case "java.lang.Long" -> Long.class;
			case "java.lang.Boolean" -> Boolean.class;
			case "java.lang.Double" -> Double.class;
			case "java.lang.Float" -> Float.class;
			case "java.lang.String" -> String.class;
			default -> Object.class;
		};
	}

	public static Object[] convertRecordToArray(@NotNull Object obj) {
		RecordComponent[] fields = obj.getClass().getRecordComponents();
		Object[] args = new Object[fields.length];
		for (int i = 0; i < fields.length; i++) {
			try {
				final Object value = fields[i].getAccessor().invoke(obj);
				args[i] = value;
			}
			catch (Exception ignore) {
				args[i] = null;
			}
		}
		return args;
	}

}
