package com.almondtools.invivoderived.visitors;

import static java.lang.Character.toLowerCase;

import java.util.IdentityHashMap;
import java.util.Map;

public class LocalVariableNameGenerator {

	private Map<Class<?>, Integer> names;

	public LocalVariableNameGenerator() {
		this.names = new IdentityHashMap<>();
	}

	public String fetchName(Class<?> clazz) {
		return base(clazz) + names.compute(clazz, (key, value) -> {
			if (value == null) {
				return 1;
			} else {
				return value + 1;
			}
		});
	}

	private String base(Class<?> clazz) {
		String simpleName = clazz.getSimpleName();
		return toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
	}
}
