package org.mvnsearch.chatgpt.model.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvnsearch.chatgpt.model.completion.chat.ResponseFormatJsonSchema;
import org.mvnsearch.chatgpt.model.output.StructuredOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class GPTFunctionUtils {

	private static final Logger log = LoggerFactory.getLogger(GPTFunctionUtils.class);

	public static final ObjectMapper objectMapper = new ObjectMapper()//
		.configure(FAIL_ON_UNKNOWN_PROPERTIES, false)//
		.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

	public static ResponseFormatJsonSchema extractOutputJsonSchema(Class<?> clazz) throws Exception {
		ResponseFormatJsonSchema jsonSchema = new ResponseFormatJsonSchema();
		jsonSchema.setName(clazz.getSimpleName());
		final StructuredOutput structuredOutput = clazz.getAnnotation(StructuredOutput.class);
		if (structuredOutput != null) {
			if (!structuredOutput.name().isEmpty()) {
				jsonSchema.setName(structuredOutput.name());
			}
			if (!structuredOutput.description().isEmpty()) {
				jsonSchema.setDescription(structuredOutput.description());
			}
			jsonSchema.setSchema(extractParameters(clazz));
		}
		return jsonSchema;
	}

	/**
	 * Extract GPT functions from class (and all parent classes/interfaces) by scanning
	 * methods with {@code @GPTFunction}.
	 * @param clazz the Java class whose hierarchy we need to search
	 * @return all functions annotated with
	 * {@code @GPTFunction the @GPTFunction annotation}
	 */
	public static Map<String, ChatGPTJavaFunction> extractFunctions(Class<?> clazz) throws Exception {
		Map<String, ChatGPTJavaFunction> functionDeclares = new HashMap<>();
		Set<Class<?>> classesToSearchForFunctions = getAllClassesInType(clazz);
		log.debug("starting to look for functions in " + clazz.getName() + ". found " + classesToSearchForFunctions);

		Set<Method> methods = classesToSearchForFunctions.stream()
			.flatMap(cc -> Stream.of(cc.getDeclaredMethods()))
			.filter(m -> m.getAnnotation(GPTFunction.class) != null)
			.collect(Collectors.toSet());

		log.debug("found " + methods.size() + " methods.");

		if (log.isDebugEnabled() && !methods.isEmpty()) {
			log.debug("found " + methods.size() + " methods");
			log.debug("======================================================================");
			log.debug("class " + clazz.getName());
			for (Method m : methods) {
				log.debug("\t " + m.getReturnType().getName() + " " + m.getName() + "("
						+ Arrays.toString(m.getParameterTypes()) + ")");
			}
		}

		for (Method method : methods) {
			GPTFunction gptFunctionAnnotation = method.getAnnotation(GPTFunction.class);
			if (gptFunctionAnnotation != null) {
				String functionName = gptFunctionAnnotation.name();
				if (functionName.isEmpty()) {
					functionName = method.getName();
				}
				ChatGPTJavaFunction gptJavaFunction = new ChatGPTJavaFunction();
				gptJavaFunction.setJavaMethod(method);
				gptJavaFunction.setName(functionName);
				gptJavaFunction.setDescription(gptFunctionAnnotation.value());
				Class<?> requestClazz = method.getParameterTypes()[0];
				gptJavaFunction.setParameterType(requestClazz);
				gptJavaFunction.setParameters(extractParameters(requestClazz));
				functionDeclares.put(functionName, gptJavaFunction);
			}
		}
		return functionDeclares;
	}

	private static Parameters extractParameters(Class<?> clazz) throws Exception {
		Parameters parameters = new Parameters("object", new HashMap<>(), new ArrayList<>());
		for (Field field : clazz.getDeclaredFields()) {
			Parameter functionParamAnnotation = field.getAnnotation(Parameter.class);
			if (functionParamAnnotation != null) {
				String fieldName = functionParamAnnotation.name();
				String fieldType = getJsonSchemaType(field.getType());
				String fieldDescription = functionParamAnnotation.value();
				if (fieldName.isEmpty()) {
					fieldName = field.getName();
				}
				if (fieldType.equals("array")) {
					Class<?> actualClazz = parseArrayItemClass(field.getGenericType());
					parameters.getProperties()
						.put(fieldName,
								new Parameters.JsonSchemaProperty(fieldName, "array", fieldDescription,
										new Parameters.JsonArrayItems(getJsonSchemaType(actualClazz), null)));
				} //
				else if (fieldType.equals("object")) {
					throw new Exception("Object type not supported: " + clazz.getName() + "." + field.getName());
				} //
				else {
					parameters.getProperties().put(fieldName, new Parameters.JsonSchemaProperty(fieldName, fieldType, fieldDescription));
				}
				if (functionParamAnnotation.required()) {
					parameters.getRequired().add(fieldName);
				} //
				else {
					for (Annotation annotation : field.getAnnotations()) {
						final String annotationName = annotation.annotationType().getName().toLowerCase();
						if (annotationName.endsWith("nonnull")) {
							parameters.getRequired().add(fieldName);
							break;
						}
					}
				}
			}
		}
		return parameters;
	}

	public static Set<Class<?>> getAllClassesInType(Class<?> clazz) {
		log.debug("finding types for " + clazz.getName());
		Set<Class<?>> all = new HashSet<>();
		all.add(clazz);
		all.addAll(Arrays.asList(ClassUtils.getAllInterfacesForClass(clazz)));
		Class<?> parent;
		Class<?> c = clazz;
		while ((parent = c.getSuperclass()) != null && !parent.equals(Object.class)) {
			all.add(parent);
			c = c.getSuperclass();
		}
		return all;
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
