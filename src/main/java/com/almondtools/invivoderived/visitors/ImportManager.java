package com.almondtools.invivoderived.visitors;

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

	public void registerImports(Class<?>... types) {
		for (Class<?> type : types) {
			registerImport(type);
		}
	}

	public void registerImport(Class<?> type) {
		if (type.isPrimitive()) {
			return;
		} else if (type.isArray()) {
			registerImport(type.getComponentType());
		} else {
			imports.add(type.getName());
		}
	}

}
