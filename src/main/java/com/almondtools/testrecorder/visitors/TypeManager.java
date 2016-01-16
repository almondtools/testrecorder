package com.almondtools.testrecorder.visitors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.almondtools.testrecorder.Wrapped;

public class TypeManager {

	private Map<Type, String> imports;
	private Set<String> staticImports;

	public TypeManager() {
		imports = new LinkedHashMap<>();
		staticImports = new LinkedHashSet<>();
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

	public void registerImport(Class<?> type) {
		if (type.isPrimitive()) {
			return;
		} else if (type.isArray()) {
			registerImport(type.getComponentType());
		} else if (isHidden(type)) {
			registerImport(Wrapped.class);
			staticImport(Wrapped.class, "clazz");
		} else {
			imports.put(type, type.getName().replace('$', '.'));
		}
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

	public static Type getArgument(Type type, int i) {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[i];
		} else {
			return wildcard();
		}
	}

	public String getBestName(Type type) {
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.getTypeParameters().length > 0) {
				return clazz.getSimpleName() + "<>";
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
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public String getSimpleName(Type type) {
		if (isHidden(type)) {
			return Wrapped.class.getSimpleName();
		} else if (type instanceof Class<?>) {
			return ((Class<?>) type).getSimpleName();
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
		if (isHidden(type)) {
			return Wrapped.class.getSimpleName();
		} else if (type instanceof Class<?>) {
			return ((Class<?>) type).getSimpleName();
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
			return getRawName((Class<?>) type) + ".class";
		}
	}

	public static boolean isPrimitive(Type type) {
		return type instanceof Class<?> && ((Class<?>) type).isPrimitive();
	}

	public String getWrappedName(Type type) {
		return "clazz(\"" + getBase(type).getName() + "\")";
	}

	public static boolean isHidden(Type type) {
		return !Modifier.isPublic(getBase(type).getModifiers());
	}

	public static Type parameterized(Type raw, Type owner, Type... typeArgs) {
		return new ParameterizedType() {

			@Override
			public Type getRawType() {
				return raw;
			}

			@Override
			public Type getOwnerType() {
				return owner;
			}

			@Override
			public Type[] getActualTypeArguments() {
				return typeArgs;
			}
		};
	}

	public static Type wildcard() {
		return new WildcardType() {

			@Override
			public Type[] getUpperBounds() {
				return new Type[0];
			}

			@Override
			public Type[] getLowerBounds() {
				return new Type[0];
			}
		};
	}

}
