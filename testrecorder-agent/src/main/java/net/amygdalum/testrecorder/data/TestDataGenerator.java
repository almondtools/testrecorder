package net.amygdalum.testrecorder.data;

import java.util.IdentityHashMap;
import java.util.Map;

import net.amygdalum.testrecorder.runtime.DefaultValue;

public class TestDataGenerator {

	private Map<Class<?>, TestValueGenerator<?>> objectValues;

	public TestDataGenerator() {
		this.objectValues = new IdentityHashMap<>();
	}

	public TestDataGenerator withValues(Class<?> clazz, TestValueGenerator<?> generator) {
		objectValues.put(clazz, generator);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T create(Class<T> clazz) {
		TestValueGenerator<T> gen = (TestValueGenerator<T>) objectValues.computeIfAbsent(clazz, c -> new DefaultValueGenerator<>(c));
		return gen.create(this);
	}

	private static class DefaultValueGenerator<T> implements TestValueGenerator<T> {
		
		private Class<T> clazz;

		public DefaultValueGenerator(Class<T> clazz) {
			this.clazz = clazz;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T create(TestDataGenerator generator) {
			return (T) DefaultValue.of(clazz);
		}
	}

}
