package net.amygdalum.testrecorder.data;

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
		Class<?> current = clazz;
		while (current != Object.class) {
			generateFields(generator, instance, current);
			current = current.getSuperclass();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private T makeInstance(TestDataGenerator generator) {
		try {
			return clazz.newInstance();
		} catch (ReflectiveOperationException e) {
		}
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			boolean accessible = constructor.isAccessible();
			try {
				if (!accessible) {
					constructor.setAccessible(true);
				}
				Object[] args = createArgs(generator, constructor.getParameterTypes());
				return (T) constructor.newInstance(args);
			} catch (ReflectiveOperationException e) {
			} finally {
				if (!accessible) {
					constructor.setAccessible(false);
				}
			}
		}
		return null;

	}

	private Object[] createArgs(TestDataGenerator generator, Class<?>[] parameterTypes) {
		Object[] args = new Object[parameterTypes.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = generator.create(clazz);
		}
		return args;
	}

	private void generateFields(TestDataGenerator generator, T instance, Class<?> current) {
		for (Field field : current.getDeclaredFields()) {
			if (field.isSynthetic()) {
				continue;
			}
			boolean accessible = field.isAccessible();
			try {
				if (!accessible) {
					field.setAccessible(true);
				}
				field.set(instance, generator.create(field.getType()));
			} catch (ReflectiveOperationException e) {
			} finally {
				if (!accessible) {
					field.setAccessible(false);
				}
			}
		}
	}

}
