package com.almondtools.testrecorder.visitors;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

public class ImportManager {

	private Set<String> imports;

	public ImportManager() {
		imports = new LinkedHashSet<>();
	}

	public Set<String> getImports() {
		return imports;
	}

	public void staticImport(Class<?> type, String method) {
		imports.add("static " + type.getName() + "." + method);
	}

	public void registerImports(Type... types) {
		for (Type type : types) {
			registerImport(type);
		}
	}

	public void registerImport(Type type) {
		if (type instanceof Class<?>) {
			registerImport((Class<?>)type);
		} else if (type instanceof GenericArrayType) {
			registerImport(((GenericArrayType) type).getGenericComponentType());
		} else if (type instanceof ParameterizedType) {
			registerImport(((ParameterizedType) type).getRawType());
			registerImports(((ParameterizedType) type).getActualTypeArguments());
		}
	}

	public void registerImport(Class<?> type) {
		if (type.isPrimitive()) {
			return;
		} else if (type.isArray()) {
			registerImport(type.getComponentType());
		} else {
			imports.add(type.getName().replace('$', '.'));
		}
	}

}
