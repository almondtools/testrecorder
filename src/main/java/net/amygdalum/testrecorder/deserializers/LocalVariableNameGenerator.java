package net.amygdalum.testrecorder.deserializers;

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
			return base(((GenericArrayType) type).getGenericComponentType()) + "Array";
		} else if (type instanceof ParameterizedType) {
			return base(((ParameterizedType) type).getRawType());
		} else {
			return normalize(type.getTypeName());
		}
	}

	private String base(Class<?> clazz) {
		if (clazz.isArray()) {
			return base(clazz.getComponentType()) + "Array";
		} else {
			return variableName(clazz);
		}
	}

	private String variableName(Class<?> clazz) {
		String variableName = clazz.getSimpleName();
		if (variableName.isEmpty()) {
			String fullName = clazz.getName();
			int lastdot = fullName.lastIndexOf('.');
			variableName = fullName.substring(lastdot + 1);
		}
		return normalize(variableName);
	}

	private String normalize(String name) {
		if (name.isEmpty()) {
			return "_";
		}
		name = name.replaceAll("[^\\w$]+", "_").replaceAll("^_+|_+$","");
		char lastChar = name.charAt(name.length() - 1);
		if (Character.isDigit(lastChar)) {
			name = name + '_';
		}
		return toLowerCase(name.charAt(0)) + name.substring(1);
	}

}
