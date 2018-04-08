package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.runtime.Params.NONE;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.getDeclaredField;
import static net.amygdalum.testrecorder.util.Types.isFinal;
import static net.amygdalum.testrecorder.util.Types.isStatic;
import static net.amygdalum.testrecorder.util.Types.isUnhandledSynthetic;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.util.Instantiations;
import net.amygdalum.testrecorder.util.Types;

public abstract class GenericObject {

	public static <T> T forward(Class<T> clazz) {
		return newInstance(clazz);
	}

	public static Wrapped forward(Wrapped wrapped) {
		return wrapped;
	}

	public static void define(Object o, GenericObject genericObject) {
		Object value = o instanceof Wrapped ? ((Wrapped) o).value() : o;
		for (Field field : genericObject.getGenericFields(o.getClass())) {
			try {
				accessing(field).exec(f -> setField(value, f.getName(), f.get(genericObject)));
			} catch (ReflectiveOperationException e) {
				throw new GenericObjectException("definition of object failed.", e);
			}
		}
	}

	public <T> T as(Class<T> clazz) {
		return as(newInstance(clazz));
	}

	@SuppressWarnings("unchecked")
	public static <T> T newProxy(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return null;
			}
		});
	}

	public static <T> T newEnum(Class<T> clazz) {
		try {
			Method valuesMethod = clazz.getDeclaredMethod("values");
			T value = accessing(valuesMethod).call(m -> {
				Object values = m.invoke(null);
				if (values != null && Array.getLength(values) > 0) {
					return clazz.cast(Array.get(values, 0));
				} else {
					return null;
				}
			});
			return value;
		} catch (ReflectiveOperationException e) {
			throw new GenericObjectException("creation of enum failed", e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz) {
		List<String> tries = new ArrayList<>();
		List<Throwable> suppressed = new ArrayList<>();
		try {
			for (Constructor<T> constructor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {
				try {
					return accessing(constructor).call(c -> {
						for (Params params : bestParams(c.getParameterTypes())) {
							try {
								return c.newInstance(params.values());
							} catch (ReflectiveOperationException | RuntimeException e) {
								suppressed.add(e);
								tries.add("new " + clazz.getSimpleName() + params.getDescription());
							}
						}
						throw new InstantiationException();
					});
				} catch (ReflectiveOperationException e) {
					continue;
				}
			}
			return Instantiations.newInstance(clazz);
		} catch (RuntimeException | Error e) {
			suppressed.add(e);
			tries.add("newConstructorForSerialization(" + clazz.getSimpleName() + ")");
			String msg = "failed to instantiate " + clazz.getName() + ", tried:\n" + tries.stream()
				.map(trie -> "\t- " + trie)
				.collect(joining("\n")) + ":";
			throw new GenericObjectException(msg, suppressed.toArray(new Throwable[0]));
		}
	}

	private static Iterable<Params> bestParams(Class<?>[] parameterTypes) {
		if (parameterTypes.length == 0) {
			return asList(NONE);
		} else {
			return asList(
				new Params(parameterTypes, DefaultValue.INSTANCE),
				new Params(parameterTypes, NonNullValue.INSTANCE),
				new Params(parameterTypes, NonDefaultValue.INSTANCE));
		}
	}

	public <T> T as(Supplier<T> constructor) {
		return as(constructor.get());
	}

	public Wrapped as(Wrapped wrapped) {
		for (Field field : getGenericFields(wrapped.getWrappedClass())) {
			try {
				accessing(field).exec(f -> wrapped.setField(f.getName(), f.get(this)));
			} catch (ReflectiveOperationException e) {
				throw new GenericObjectException("setting fields failed:", e);
			}
		}
		return wrapped;
	}

	public <T> T as(T o) {
		for (Field field : getGenericFields(o.getClass())) {
			try {
				accessing(field).exec(f -> setField(o, f.getName(), f.get(this)));
			} catch (ReflectiveOperationException e) {
				throw new GenericObjectException("settings fields failed:", e);
			}
		}
		return o;
	}

	public static void setField(Object o, String name, Object value) {
		if (value instanceof Wrapped) {
			value = ((Wrapped) value).value();
		}
		Field to = findField(name, o.getClass());
		if (isNonAssignableArray(value, to)) {
			value = copyToAssignableArray(value, to);
		}
		setField(o, to, value);
	}

	private static boolean isNonAssignableArray(Object value, Field to) {
		return to.getType().isArray()
			&& value != null
			&& value.getClass().isArray()
			&& !to.getType().isAssignableFrom(value.getClass());
	}

	private static Object copyToAssignableArray(Object array, Field to) {
		int length = Array.getLength(array);
		Object value = Array.newInstance(to.getType().getComponentType(), length);
		for (int i = 0; i < length; i++) {
			Array.set(value, i, Array.get(array, i));
		}
		return value;
	}

	public static void setField(Object o, Field to, Object value) {
		try {
			accessing(to).exec(f -> f.set(o, value));
		} catch (ReflectiveOperationException e) {
			throw new GenericObjectException("settings field " + to.getName() + " failed:", e);
		}
	}

	public static void copyArrayValues(Object from, Object to) {
		int fromLength = Array.getLength(from);
		int toLength = Array.getLength(to);
		if (fromLength != toLength) {
			throw new GenericObjectException("copying array failed:", new ArrayIndexOutOfBoundsException());
		}
		for (int i = 0; i < fromLength; i++) {
			Object value = Array.get(from, i);
			Array.set(to, i, value);
		}
	}

	public static void copyField(Field field, Object from, Object to) {
		try {
			accessing(field).exec(f -> {
				Object value = f.get(from);
				f.set(to, value);
			});
		} catch (ReflectiveOperationException e) {
			throw new GenericObjectException("copying field " + field.getName() + " failed:", e);
		}
	}

	public static Field findField(String name, Class<?> clazz) {
		try {
			Optional<Field> field = getQualifiedField(clazz, name);
			if (field.isPresent()) {
				return field.get();
			}
			return getDeclaredField(clazz, name);
		} catch (NoSuchFieldException e) {
			throw new GenericObjectException("field " + name + " not found:", e);
		}
	}

	public List<Field> getGenericFields(Class<?> clazz) {
		Field[] declaredFields = getClass().getDeclaredFields();
		return Stream.of(declaredFields)
			.filter(field -> isSerializable(clazz, field))
			.collect(toList());
	}

	private boolean isSerializable(Class<?> clazz, Field field) {
		if (isUnhandledSynthetic(field) && !getQualifiedField(clazz, field.getName()).isPresent()) {
			return false;
		}
		return !isStatic(field)
			&& !isFinal(field);
	}

	public static Optional<Field> getQualifiedField(Class<?> clazz, String name) {
		int nameSeparatorPos = name.lastIndexOf('$');
		if (nameSeparatorPos < 0) {
			return Optional.empty();
		}
		String className = name.substring(0, nameSeparatorPos).replace('$', '.');
		String fieldName = name.substring(nameSeparatorPos + 1);
		return Types.getDeclaredFields(clazz, fieldName).stream()
			.filter(field -> field.getDeclaringClass().getCanonicalName() != null)
			.filter(field -> field.getDeclaringClass().getCanonicalName().equals(className) || field.getDeclaringClass().getSimpleName().equals(className))
			.findFirst();
	}

}
