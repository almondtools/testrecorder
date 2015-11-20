package com.almondtools.testrecorder;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;

public final class TypeHelper {

	private TypeHelper() {
	}
	
	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

	public static Field getDeclaredField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}

	public static Class<?> getBase(Type type) {
		if (type instanceof Class<?>) {
			return ((Class<?>) type);
		} else if (type instanceof GenericArrayType) {
			return Array.newInstance(getBase(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
		} else if (type instanceof ParameterizedType) {
			return getBase(((ParameterizedType) type).getRawType());
		} else {
			return Object.class;
		}
	}

	public static String getSimpleName(Type type) {
		if (type instanceof Class<?>) {
			return ((Class<?>) type).getSimpleName();
		} else if (type instanceof GenericArrayType) {
			return getSimpleName(((GenericArrayType) type).getGenericComponentType()) + "[]";
		} else if (type instanceof ParameterizedType) {
			return getSimpleName(((ParameterizedType) type).getRawType())
				+ Stream.of(((ParameterizedType) type).getActualTypeArguments())
					.map(argtype -> getSimpleName(argtype))
					.collect(joining(", ", "<", ">"));
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public static String getRawName(Type type) {
		if (type instanceof Class<?>) {
			return ((Class<?>) type).getSimpleName();
		} else if (type instanceof GenericArrayType) {
			return getRawName(((GenericArrayType) type).getGenericComponentType()) + "[]";
		} else if (type instanceof ParameterizedType) {
			return getRawName(((ParameterizedType) type).getRawType());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public static boolean isPrimitive(Type type) {
		return type instanceof Class<?> && ((Class<?>) type).isPrimitive();
	}

}
