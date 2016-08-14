package net.amygdalum.testrecorder.deserializers;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.Wrapped;
import net.amygdalum.testrecorder.util.Types;

public class TypeManager {

	private String pkg;
	private Map<String, String> imports;
	private Set<String> staticImports;
	private Set<Type> noImports;

	public TypeManager() {
		this("");
	}
	
	public TypeManager(String pkg) {
		this.pkg = pkg;
		this.imports = new LinkedHashMap<>();
		this.staticImports = new LinkedHashSet<>();
		this.noImports = new LinkedHashSet<>();
	}
	
	public String getPackage() {
		return pkg;
	}
	
	public List<String> getImports() {
		return Stream.concat(imports.values().stream(), staticImports.stream())
			.collect(toList());
	}

	public void staticImport(Class<?> type, String method) {
		staticImports.add("static " + type.getName() + "." + method);
	}

	public void registerTypes(Type... types) {
		for (Type type : types) {
			registerType(type);
		}
	}

	public void registerType(Type type) {
		if (type instanceof Class<?>) {
			registerImport((Class<?>) type);
		} else if (type instanceof GenericArrayType) {
			registerType(((GenericArrayType) type).getGenericComponentType());
		} else if (type instanceof ParameterizedType) {
			registerType(((ParameterizedType) type).getRawType());
			registerTypes(((ParameterizedType) type).getActualTypeArguments());
		}
	}

	public void registerImport(Class<?> clazz) {
		if (noImports.contains(clazz)) {
			return;
		} else if (isHidden(clazz)) {
			registerImport(Wrapped.class);
			staticImport(Wrapped.class, "clazz");
			noImports.add(clazz);
		} else if (imports.containsKey(clazz.getSimpleName())) {
			if (!imports.get(clazz.getSimpleName()).equals(getFullName(clazz))) {
				noImports.add(clazz);
			}
		} else if (clazz.isPrimitive()) {
			return;
		} else if (clazz.isArray()) {
			registerImport(clazz.getComponentType());
		} else {
			imports.put(clazz.getSimpleName(), getFullName(clazz));
		}
	}

	public String getFullName(Class<?> clazz) {
		return clazz.getName().replace('$', '.');
	}

	public String getBestName(Type type) {
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			String base = getSimpleName(clazz);
			String generics = clazz.getTypeParameters().length > 0 ? "<>" : "";
			return base + generics;
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
	
	public String getBestSignature(Type type) {
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			String base = getSimpleSignature(clazz);
			String generics = clazz.getTypeParameters().length > 0 ? "<>" : "";
			return base + generics;
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

	public String getSimpleName(Type type) {
		return getSimpleSignature(type).replace('$', '.');
	}

	public String getSimpleSignature(Type type) {
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (noImports.contains(clazz)) {
				return clazz.getName();
			} else {
				return clazz.getSimpleName();
			}
		} else if (type instanceof GenericArrayType) {
			return getSimpleName(((GenericArrayType) type).getGenericComponentType()) + "[]";
		} else if (type instanceof ParameterizedType) {
			return getSimpleName(((ParameterizedType) type).getRawType())
				+ Stream.of(((ParameterizedType) type).getActualTypeArguments())
					.map(argtype -> getSimpleName(argtype))
					.collect(joining(", ", "<", ">"));
		} else if (type instanceof WildcardType) {
			return "?";
		} else {
			throw new UnsupportedOperationException();
		}
	}
	public String getRawName(Type type) {
		if (type instanceof Class<?>) {
			return getSimpleName(type);
		} else if (type instanceof GenericArrayType) {
			return getRawName(((GenericArrayType) type).getGenericComponentType()) + "[]";
		} else if (type instanceof ParameterizedType) {
			return getRawName(((ParameterizedType) type).getRawType());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public String getRawTypeName(Type type) {
		if (isHidden(type)) {
			return getWrappedName(type);
		} else {
			return getRawName(type) + ".class";
		}
	}


	public boolean isHidden(Constructor<?> constructor) {
		return Types.isHidden(constructor, pkg);
	}

	public boolean isHidden(Type type) {
		return Types.isHidden(type, pkg);
	}

	public Type wrapHidden(Type type) {
		if (isHidden(type)) {
			return Wrapped.class;
		} else {
			return type;
		}
	}

	public String getWrappedName(Type type) {
		return "clazz(\"" + baseType(type).getName() + "\")";
	}

}
