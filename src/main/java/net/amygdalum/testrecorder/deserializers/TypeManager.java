package net.amygdalum.testrecorder.deserializers;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.Wrapped;

public class TypeManager {

	private Map<String, String> imports;
	private Set<String> staticImports;
	private Set<Type> noImports;

	public TypeManager() {
		imports = new LinkedHashMap<>();
		staticImports = new LinkedHashSet<>();
		noImports = new LinkedHashSet<>();
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

	public static Type inferType(List<Type> types) {
		Optional<Class<?>> reduce = types.stream()
			.map(type -> superTypes(baseType(type)))
			.reduce((s1, s2) -> intersectClasses(s1, s2))
			.map(s -> bestClass(s));
		return reduce.orElse(Object.class);
	}

	private static Set<Class<?>> superTypes(Class<?> clazz) {
		Set<Class<?>> done = new LinkedHashSet<>();
		Queue<Class<?>> todo = new LinkedList<>();
		todo.add(clazz);
		while (!todo.isEmpty()) {
			Class<?> next = todo.remove();
			if (!done.contains(next) && next != Object.class) {
				Class<?> superclass = next.getSuperclass();
				if (superclass != null) {
					todo.add(superclass);
				}
				for (Class<?> nextInterface : next.getInterfaces()) {
					todo.add(nextInterface);
				}
			}
		}
		return done;
	}

	private static Set<Class<?>> intersectClasses(Set<Class<?>> s1, Set<Class<?>> s2) {
		Set<Class<?>> result = new LinkedHashSet<>(s1);
		result.retainAll(s2);
		return result;
	}

	private static Class<?> bestClass(Set<Class<?>> classes) {
		Class<?> bestInterface = null;
		Class<?> bestClass = Object.class;
		for (Class<?> clazz : classes) {
			if (clazz.isInterface()) {
				if (bestInterface == null) {
					bestInterface = clazz;
				} else if (bestInterface.isAssignableFrom(clazz)) {
					bestInterface = clazz;
				}
			} else if (bestClass.isAssignableFrom(clazz)) {
				bestClass = clazz;
			}
		}
		if (bestInterface != null) {
			return bestInterface;
		} else {
			return bestClass;
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

	public static Class<?> baseType(Type type) {
		if (type instanceof Class<?>) {
			return ((Class<?>) type);
		} else if (type instanceof GenericArrayType) {
			return Array.newInstance(baseType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
		} else if (type instanceof ParameterizedType) {
			return baseType(((ParameterizedType) type).getRawType());
		} else {
			return Object.class;
		}
	}

	public static Type array(Type componentType) {
		return new GenericArrayTypeImplementation(componentType);
	}

	public static Type component(Type arrayType) {
		if (arrayType instanceof Class<?> && ((Class<?>) arrayType).isArray()) {
			return ((Class<?>) arrayType).getComponentType();
		} else if (arrayType instanceof GenericArrayType) {
			return ((GenericArrayType) arrayType).getGenericComponentType();
		} else {
			return Object.class;
		}
	}

	public static boolean equalTypes(Type type1, Type type2) {
		return baseType(type1).equals(baseType(type2));
	}

	public static Optional<Type> typeArgument(Type type, int i) {
		if (type instanceof ParameterizedType) {
			return Optional.of(((ParameterizedType) type).getActualTypeArguments()[i]);
		} else {
			return Optional.empty();
		}
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

	public String getSimpleName(Type type) {
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (noImports.contains(clazz)) {
				return clazz.getName().replace('$', '.');
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

	public static boolean isPrimitive(Type type) {
		return type instanceof Class<?> && ((Class<?>) type).isPrimitive();
	}

	public String getWrappedName(Type type) {
		return "clazz(\"" + baseType(type).getName() + "\")";
	}

	public static Type wrapHidden(Type type) {
		if (isHidden(type)) {
			return Wrapped.class;
		} else {
			return type;
		}
	}

	public static boolean isHidden(Type type) {
		return !Modifier.isPublic(baseType(type).getModifiers());
	}

	public static Type parameterized(Type raw, Type owner, Type... typeArgs) {
		return new ParameterizedTypeImplementation(raw, owner, typeArgs);
	}

	public static Type wildcard() {
		return new WildcardTypeImplementation();
	}

	private static final class GenericArrayTypeImplementation implements GenericArrayType {

		private Type componentType;

		public GenericArrayTypeImplementation(Type componentType) {
			this.componentType = componentType;
		}

		@Override
		public Type getGenericComponentType() {
			return componentType;
		}
	}

	private static final class ParameterizedTypeImplementation implements ParameterizedType {

		private Type raw;
		private Type owner;
		private Type[] typeArgs;

		public ParameterizedTypeImplementation(Type raw, Type owner, Type... typeArgs) {
			this.raw = raw;
			this.owner = owner;
			this.typeArgs = typeArgs;
		}

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
	}

	private static final class WildcardTypeImplementation implements WildcardType {
		@Override
		public Type[] getUpperBounds() {
			return new Type[0];
		}

		@Override
		public Type[] getLowerBounds() {
			return new Type[0];
		}
	}

}
