package org.mvnsearch.chatgpt.spring.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

class TypeCrawlerTest {

	record Owner(String name) {
	}

	enum PetType {

		GOOD, GOODEST

	}

	class CatFactory implements FactoryBean<Cat> {

		CatFactory(Date date, Instant i) {
		}

		@Override
		public Cat getObject() throws Exception {
			return new Cat(PetType.GOODEST, new Owner("You"));
		}

		@Override
		public Class<?> getObjectType() {
			return Cat.class;
		}

	}

	record Cat(PetType p, Owner owner) {
		void meow(Runnable runnable) {
		}
	}

	@Test
	void crawl() {
		Set<Type> seen = TypeCrawler.crawl(CatFactory.class);
		Assertions.assertTrue(seen.contains(PetType.class));
		Assertions.assertTrue(seen.contains(Owner.class));
		Assertions.assertTrue(seen.contains(Runnable.class));
		Assertions.assertTrue(seen.contains(String.class));
		Assertions.assertTrue(seen.contains(CatFactory.class));
		Assertions.assertTrue(seen.contains(Instant.class));
		Assertions.assertTrue(seen.contains(Date.class));

	}

}
