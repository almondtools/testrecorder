package net.amygdalum.testrecorder.util;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;

public class Types {

	private Types() {
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
		Class<?> current = clazz;
		while (current != Object.class) {
			try {
				return current.getDeclaredMethod(name, parameterTypes);
			} catch (NoSuchMethodException e) {
				current = current.getSuperclass();
			}
		}
		return null;
	}

	public static Field getDeclaredField(Class<?> clazz, String name) {
		Class<?> current = clazz;
		while (current != Object.class) {
			try {
				return current.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				current = current.getSuperclass();
			}
		}
		return null;
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

	public static Class<?> boxedType(Type type) {
		if (!(type instanceof Class<?>)) {
			return baseType(type);
		}
		Class<?> clazz = (Class<?>) type;
		if (clazz == boolean.class) {
			return Boolean.class;
		} else if (clazz == char.class) {
			return Character.class;
		} else if (clazz == byte.class) {
			return Byte.class;
		} else if (clazz == short.class) {
			return Short.class;
		} else if (clazz == int.class) {
			return Integer.class;
		} else if (clazz == float.class) {
			return Float.class;
		} else if (clazz == long.class) {
			return Long.class;
		} else if (clazz == double.class) {
			return Double.class;
		} else {
			return clazz;
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

	public static boolean subsumingTypes(Type superType, Type subType) {
		return baseType(superType).isAssignableFrom(baseType(subType));
	}

	public static boolean equalTypes(Type type1, Type type2) {
		return baseType(type1).equals(baseType(type2));
	}

	public static boolean boxingEquivalentTypes(Type type1, Type type2) {
		return boxedType(type1).equals(boxedType(type2));
	}
	
	public static Optional<Type> typeArgument(Type type, int i) {
		if (type instanceof ParameterizedType) {
			Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
			if (actualTypeArguments == null || actualTypeArguments.length <= i) {
				return Optional.empty();
			}
			return Optional.of(actualTypeArguments[i]);
		} else {
			return Optional.empty();
		}
	}

	public static Class<?> innerType(Class<?> clazz, String name) {
		for (Class<?> inner : clazz.getDeclaredClasses()) {
			if (inner.getSimpleName().equals(name)) {
				return inner;
			}
		}
		throw new TypeNotPresentException(clazz.getName() + "$" + name, new ClassNotFoundException(clazz.getName() + "$" + name));
	}

	public static boolean isHidden(Type type, String pkg) {
		Class<?> clazz = baseType(type);
		int modifiers = clazz.getModifiers();
		if (isPublic(modifiers)) {
			return false;
		} else if (isPrivate(modifiers)) {
			return true;
		} else if (clazz.getEnclosingClass() != null) {
			return true;
		} else {
			return !pkg.equals(clazz.getPackage().getName());
		}
	}

	public static boolean isHidden(Constructor<?> constructor, String pkg) {
		int modifiers = constructor.getModifiers();
		if (isPublic(modifiers)) {
			return isHidden(constructor.getDeclaringClass(), pkg);
		} else if (isPrivate(modifiers)) {
			return true;
		} else if (constructor.getDeclaringClass().getEnclosingClass() != null) {
			return true;
		} else {
			return isHidden(constructor.getDeclaringClass(), pkg);
		}
	}

	public static boolean isPrimitive(Type type) {
		return type instanceof Class<?> && ((Class<?>) type).isPrimitive();
	}

	public static Type parameterized(Type raw, Type owner, Type... typeArgs) {
		return new ParameterizedTypeImplementation(raw, owner, typeArgs);
	}

	public static Type wildcard() {
		return new WildcardTypeImplementation();
	}

	public static Type wildcardExtends(Type... bounds) {
		return new WildcardTypeImplementation().extending(bounds);
	}

	public static Type wildcardSuper(Type... bounds) {
		return new WildcardTypeImplementation().limiting(bounds);
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

		@Override
		public String getTypeName() {
			StringBuilder buffer = new StringBuilder();
			buffer.append(componentType.getTypeName());
			buffer.append("[]");
			return buffer.toString();
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

		@Override
		public String getTypeName() {
			StringBuilder buffer = new StringBuilder();
			buffer.append(raw.getTypeName());
			buffer.append('<');
			if (typeArgs != null && typeArgs.length > 0) {
				buffer.append(Stream.of(typeArgs)
					.map(type -> type.getTypeName())
					.collect(joining(", ")));
			}
			buffer.append('>');
			return buffer.toString();
		}
	}

	private static final class WildcardTypeImplementation implements WildcardType {

		private Type[] upperBounds;
		private Type[] lowerBounds;

		public WildcardTypeImplementation() {
			upperBounds = new Type[0];
			lowerBounds = new Type[0];
		}

		public WildcardTypeImplementation extending(Type... bounds) {
			this.upperBounds = bounds;
			return this;
		}

		public WildcardTypeImplementation limiting(Type... bounds) {
			this.lowerBounds = bounds;
			return this;
		}

		@Override
		public Type[] getUpperBounds() {
			return upperBounds;
		}

		@Override
		public Type[] getLowerBounds() {
			return lowerBounds;
		}

		@Override
		public String getTypeName() {
			StringBuilder buffer = new StringBuilder();
			buffer.append("?");
			if (lowerBounds != null && lowerBounds.length > 0) {
				buffer.append(" super ").append(Stream.of(lowerBounds)
					.map(type -> type.getTypeName())
					.collect(joining(", ")));
			}
			if (upperBounds != null && upperBounds.length > 0) {
				buffer.append(" extends ").append(Stream.of(upperBounds)
					.map(type -> type.getTypeName())
					.collect(joining(", ")));
			}
			return buffer.toString();
		}
	}

}
