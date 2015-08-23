package com.almondtools.invivoderived.visitors;

import static java.lang.Character.toLowerCase;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LocalVariableNameGenerator {

	private Map<String, Integer> names;

	public LocalVariableNameGenerator() {
		this.names = new HashMap<>();
	}

	public String fetchName(Type type) {
		String base = base(type);
		return base + names.compute(base, (key, value) -> {
			if (value == null) {
				return 1;
			} else {
				return value + 1;
			}
		});
	}

	private String base(Type type) {
		if (type instanceof Class<?>) {
			return base((Class<?>) type);
		} else if (type instanceof GenericArrayType) {
			return base(((GenericArrayType) type).getGenericComponentType()) + "_";
		} else if (type instanceof ParameterizedType) {
			return base(((ParameterizedType) type).getRawType());
		} else {
			return type.getTypeName().replace('.', '_');
		}
	}

	private String base(Class<?> clazz) {
		if (clazz.isArray()) {
			return base(clazz.getComponentType()) + "_";
		} else {
			String simpleName = clazz.getSimpleName();
			return toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
		}
	}
}
