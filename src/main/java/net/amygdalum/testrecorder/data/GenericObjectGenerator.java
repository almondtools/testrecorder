package net.amygdalum.testrecorder.data;

import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.allFields;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class GenericObjectGenerator<T> implements TestValueGenerator<T> {

	private Class<T> clazz;

	public GenericObjectGenerator(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T create(TestDataGenerator generator) {
		T instance = makeInstance(generator);
		for (Field field : allFields(clazz)) {
			generateField(field, generator, instance);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private T makeInstance(TestDataGenerator generator) {
		try {
			return clazz.newInstance();
		} catch (ReflectiveOperationException | RuntimeException e) {
		}
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			try {
				return accessing(constructor).call(() -> {
					Object[] args = createArgs(generator, constructor.getParameterTypes());
					return (T) constructor.newInstance(args);
				});
			} catch (ReflectiveOperationException e) {
			}
		}
		return null;

	}

	private Object[] createArgs(TestDataGenerator generator, Class<?>[] parameterTypes) {
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = generator.create(parameterTypes[i]);
		}
		return args;
	}

	public void generateField(Field field, TestDataGenerator generator, T instance) {
		if (field.isSynthetic()) {
			return;
		}
		try {
			accessing(field).exec(() -> field.set(instance, generator.create(field.getType())));
		} catch (ReflectiveOperationException e) {
		}
	}

}
