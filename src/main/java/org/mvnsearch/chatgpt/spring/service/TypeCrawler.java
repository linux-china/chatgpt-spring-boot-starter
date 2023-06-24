package org.mvnsearch.chatgpt.spring.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

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
		} //

	}

}
