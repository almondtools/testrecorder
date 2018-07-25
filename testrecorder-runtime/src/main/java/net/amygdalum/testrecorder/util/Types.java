package net.amygdalum.testrecorder.util;

import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

public final class Types {

	private static final Type[] NO_TYPES = new Type[0];
	private static final String SYNTHETIC_INDICATOR = "$";
	private static final String[] HANDLED_SYNTHETIC_PREFIXES = { "this$", "val$" };

	private static Map<Type, Type> serializables = new HashMap<>();

	private Types() {
	}

	public static Type inferType(Type... types) {
		Optional<Class<?>> inferred = Arrays.stream(types)
			.map(type -> superTypes(baseType(type)))
			.reduce((s1, s2) -> intersectClasses(s1, s2))
			.map(s -> bestType(s));
		return inferred.orElse(Object.class);
	}

	public static Optional<Type> mostSpecialOf(Type... types) {
		return Arrays.stream(types)
			.filter(Objects::nonNull)
			.sorted(Types::byMostConcreteGeneric)
			.findFirst();
	}

	private static Set<Class<?>> superTypes(Class<?> clazz) {
		IdentityWorkSet<Class<?>> todo = new IdentityWorkSet<>();
		todo.add(clazz);
		while (!todo.isEmpty()) {
			Class<?> next = todo.remove();
			if (next != Object.class) {
				Class<?> superclass = next.getSuperclass();
				if (superclass != null) {
					todo.add(superclass);
				}
				for (Class<?> nextInterface : next.getInterfaces()) {
					todo.add(nextInterface);
				}
			}
		}
		return todo.getDone();
	}

	private static Set<Class<?>> intersectClasses(Set<Class<?>> s1, Set<Class<?>> s2) {
		SortedSet<Class<?>> result = new TreeSet<>(new Comparator<Class<?>>() {

			@Override
			public int compare(Class<?> o1, Class<?> o2) {
				if (o1.isAssignableFrom(o2)) {
					return -1;
				} else if (o2.isAssignableFrom(o1)) {
					return 1;
				}
				int m1 = o1.getMethods().length;
				int m2 = o2.getMethods().length;

				if (m2 > m1) {
					return 1;
				} else if (m1 > m2) {
					return -1;
				} else {
					return System.identityHashCode(o1) - System.identityHashCode(o2);
				}
			}
		});
		result.addAll(s1);
		result.retainAll(s2);
		return result;
	}

	private static Class<?> bestType(Set<Class<?>> classes) {
		Class<?> bestInterface = null;
		Class<?> bestClass = Object.class;
		for (Class<?> clazz : classes) {
			if (clazz.isInterface()) {
				if (bestInterface == null) {
					bestInterface = clazz;
				} else if (bestInterface.isAssignableFrom(clazz)) {
					bestInterface = clazz;
				} else if (estimatedInterfaceMethodCount(bestInterface) < estimatedInterfaceMethodCount(clazz)) {
					bestInterface = clazz;
				}
			} else if (bestClass.isAssignableFrom(clazz)) {
				bestClass = clazz;
			}
		}
		if (isBoxedPrimitive(bestClass)
			|| bestClass == String.class) {
			return bestClass;
		} else if (bestInterface != null) {
			return bestInterface;
		} else {
			return bestClass;
		}
	}

	public static Type resolve(Type type, Class<?> context) {
		Map<TypeVariable<?>, Type> freeVariables = computeTypeVariableResolutions(type, context);
		return updateTypes(type, freeVariables);
	}

	private static Type updateTypes(Type type, Map<TypeVariable<?>, Type> resolved) {
		if (resolved.isEmpty()) {
			return type;
		}
		if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Type resolvedType = updateTypes(componentType, resolved);
			if (resolvedType != componentType) {
				return Types.array(resolvedType);
			}
		} else if (type instanceof ParameterizedType) {
			Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
			Type ownerType = ((ParameterizedType) type).getOwnerType();
			Type rawType = ((ParameterizedType) type).getRawType();

			Type[] resolvedTypeArguments = Arrays.stream(actualTypeArguments)
				.map(typeArgument -> updateTypes(typeArgument, resolved))
				.toArray(Type[]::new);

			if (!Arrays.equals(resolvedTypeArguments, actualTypeArguments)) {
				return Types.parameterized(rawType, ownerType, resolvedTypeArguments);
			}
		} else if (type instanceof TypeVariable<?>) {
			Type resolvedType = resolved.get(type);
			if (resolvedType != null) {
				return resolvedType;
			}
		} else if (type instanceof WildcardType) {
			Type[] upperBounds = ((WildcardType) type).getUpperBounds();
			Type[] lowerBounds = ((WildcardType) type).getLowerBounds();
			if (lowerBounds.length > 0) {
				Type[] resolvedLowerBounds = Arrays.stream(lowerBounds)
					.map(bound -> updateTypes(bound, resolved))
					.toArray(Type[]::new);
				if (!Arrays.equals(resolvedLowerBounds, lowerBounds)) {
					return wildcardSuper(resolvedLowerBounds);
				}
			} else if (upperBounds.length > 0 && upperBounds[0] != Object.class) {
				Type[] resolvedUpperBounds = Arrays.stream(upperBounds)
					.map(bound -> updateTypes(bound, resolved))
					.toArray(Type[]::new);
				if (!Arrays.equals(resolvedUpperBounds, upperBounds)) {
					return wildcardExtends(resolvedUpperBounds);
				}
			}
			return type;
		}
		return type;
	}

	private static Map<TypeVariable<?>, Type> computeTypeVariableResolutions(Type type, Class<?> context) {
		Map<TypeVariable<?>, Type> typeVariables = computeFreeTypeVariables(type);
		Set<ParameterizedType> typeVariableDefinitions = computeTypeVariableDefinitions(context);
		Map<TypeVariable<?>, Type> next = new HashMap<>(typeVariables);
		while (!next.isEmpty()) {
			Iterator<Map.Entry<TypeVariable<?>, Type>> nexts = next.entrySet().iterator();
			while (nexts.hasNext()) {
				Entry<TypeVariable<?>, Type> entry = nexts.next();
				TypeVariable<?> key = entry.getKey();
				Type value = entry.getValue();
				if (value instanceof TypeVariable<?>) {
					Type resolved = resolve((TypeVariable<?>) value, typeVariableDefinitions);
					if (value != resolved) {
						entry.setValue(resolved);
						typeVariables.put(key, resolved);
						continue;
					}
				}
				nexts.remove();
			}
		}
		typeVariables.entrySet().removeIf(entry -> entry.getValue() instanceof TypeVariable<?>);
		return typeVariables;
	}

	private static Type resolve(TypeVariable<? extends GenericDeclaration> value, Set<ParameterizedType> types) {
		GenericDeclaration genericDeclaration = value.getGenericDeclaration();
		if (genericDeclaration instanceof Class<?>) {
			Class<?> clazz = (Class<?>) genericDeclaration;
			Optional<ParameterizedType> matching = types.stream()
				.filter(type -> type.getRawType() == clazz)
				.sorted(Types::byMostConcrete)
				.findFirst();
			if (matching.isPresent()) {
				TypeVariable<?>[] params = genericDeclaration.getTypeParameters();
				Type[] actual = matching.get().getActualTypeArguments();
				for (int i = 0; i < params.length && i < actual.length; i++) {
					if (params[i] == value) {
						return actual[i];
					}
				}
			}
		}
		return value;
	}

	public static int byMostConcrete(Type type1, Type type2) {
		if (type1 == type2) {
			return 0;
		} else if (type1 instanceof Class<?> && type2 instanceof Class<?>) {
			Class<?> clazz1 = (Class<?>) type1;
			Class<?> clazz2 = (Class<?>) type2;

			if (clazz1.isAssignableFrom(clazz2)) {
				return 1;
			} else if (clazz2.isAssignableFrom(clazz1)) {
				return -1;
			} else {
				return 0;
			}
		} else if (type1 instanceof Class<?>) {
			return -1;
		} else if (type2 instanceof Class<?>) {
			return 1;
		} else {
			return byMostConcrete(baseType(type1), baseType(type2));
		}
	}

	public static int byMostConcreteGeneric(Type type1, Type type2) {
		if (type1 == type2) {
			return 0;
		} else if (type1 instanceof Class<?> && type2 instanceof Class<?>) {
			Class<?> clazz1 = (Class<?>) type1;
			Class<?> clazz2 = (Class<?>) type2;

			if (clazz1.isAssignableFrom(clazz2)) {
				return 1;
			} else if (clazz2.isAssignableFrom(clazz1)) {
				return -1;
			} else {
				return 0;
			}
		} else if (type1 instanceof WildcardType && type2 instanceof WildcardType) {
			Type[] typeArguments1 = ((WildcardType) type1).getUpperBounds();
			Type[] typeArguments2 = ((WildcardType) type2).getUpperBounds();
			int compare = Integer.compare(typeArguments2.length, typeArguments1.length);
			if (compare == 0) {
				int len = Math.min(typeArguments1.length, typeArguments2.length);
				for (int i = 0; i < len; i++) {
					compare += byMostConcrete(typeArguments1[i], typeArguments2[i]);
				}
			}
			return compare;
		} else if (type1 instanceof WildcardType) {
			return 1;
		} else if (type2 instanceof WildcardType) {
			return -1;
		} else {
			int compare = byMostConcrete(baseType(type1), baseType(type2));
			if (compare == 0) {
				Type[] typeArguments1 = type1 instanceof ParameterizedType ? ((ParameterizedType) type1).getActualTypeArguments() : NO_TYPES;
				Type[] typeArguments2 = type2 instanceof ParameterizedType ? ((ParameterizedType) type2).getActualTypeArguments() : NO_TYPES;
				compare = Integer.compare(typeArguments2.length, typeArguments1.length);
				if (compare == 0) {
					int len = Math.min(typeArguments1.length, typeArguments2.length);
					for (int i = 0; i < len; i++) {
						compare += byMostConcrete(typeArguments1[i], typeArguments2[i]);
					}
				}
			}
			return compare;
		}
	}

	private static Map<TypeVariable<?>, Type> computeFreeTypeVariables(Type type) {
		Map<TypeVariable<?>, Type> unresolvedVariables = new HashMap<>();
		if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Map<TypeVariable<?>, Type> componentTypeVariables = computeFreeTypeVariables(componentType);
			unresolvedVariables.putAll(componentTypeVariables);
		} else if (type instanceof ParameterizedType) {
			for (Type typeArgument : ((ParameterizedType) type).getActualTypeArguments()) {
				Map<TypeVariable<?>, Type> typeArgumentVariables = computeFreeTypeVariables(typeArgument);
				unresolvedVariables.putAll(typeArgumentVariables);
			}
		} else if (type instanceof TypeVariable<?>) {
			unresolvedVariables.put((TypeVariable<?>) type, type);
		} else if (type instanceof WildcardType) {
			for (Type typeBound : ((WildcardType) type).getUpperBounds()) {
				Map<TypeVariable<?>, Type> typeBoundVariables = computeFreeTypeVariables(typeBound);
				unresolvedVariables.putAll(typeBoundVariables);
			}
			for (Type typeBound : ((WildcardType) type).getLowerBounds()) {
				Map<TypeVariable<?>, Type> typeBoundVariables = computeFreeTypeVariables(typeBound);
				unresolvedVariables.putAll(typeBoundVariables);
			}
		}
		return unresolvedVariables;
	}

	private static Set<ParameterizedType> computeTypeVariableDefinitions(Class<?> context) {
		if (context == Object.class) {
			return emptySet();
		}
		Set<ParameterizedType> types = new HashSet<>();
		Type superType = context.getGenericSuperclass();
		if (superType instanceof ParameterizedType) {
			types.add((ParameterizedType) superType);
		}
		types.addAll(computeTypeVariableDefinitions(baseType(superType)));
		for (Type interfaceType : context.getGenericInterfaces()) {
			if (!types.contains(interfaceType)) {
				if (interfaceType instanceof ParameterizedType) {
					types.add((ParameterizedType) interfaceType);
				}
				types.addAll(computeTypeVariableDefinitions(baseType(interfaceType)));
			}
		}
		return types;
	}

	public static Class<?> baseType(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
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
		} else if (clazz == void.class) {
			return Void.class;
		} else {
			return clazz;
		}
	}

	public static Type component(Type arrayType) {
		if (arrayType instanceof Class<?> && ((Class<?>) arrayType).isArray()) {
			return ((Class<?>) arrayType).getComponentType();
		} else if (arrayType instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) arrayType).getGenericComponentType();
			return isBound(componentType) ? componentType : Object.class;
		} else {
			return Object.class;
		}
	}

	public static boolean assignableTypes(Type toType, Type fromType) {
		if (toType == null) {
			return false;
		}
		if (fromType == null) {
			return true;
		}
		Class<?> toClass = baseType(toType);
		Class<?> fromClass = baseType(fromType);
		if (!toClass.isAssignableFrom(fromClass)) {
			return false;
		} else if (toType instanceof Class<?> || fromType instanceof Class<?>) {
			return true;
		} else {
			Type[] toArguments = Types.typeArguments(toType).toArray(Type[]::new);
			Type[] fromArguments = Types.typeArguments(fromType).toArray(Type[]::new);
			int length = toArguments.length == fromArguments.length ? toArguments.length : -1;
			if (length == -1) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				if (toArguments[i] instanceof WildcardType) {
					continue;
				} else if (toArguments[i] != fromArguments[i]) {
					return false;
				}
			}
			return true;
		}

	}

	public static boolean equalGenericTypes(Type type1, Type type2) {
		return type1.equals(type2)
			|| type2.equals(type1);
	}

	public static boolean equalBaseTypes(Type type1, Type type2) {
		return baseType(type1).equals(baseType(type2));
	}

	public static boolean boxingEquivalentTypes(Type type1, Type type2) {
		if (type1 instanceof Class<?> && type2 instanceof Class<?>) {
			return boxedType(type1).equals(boxedType(type2));
		}
		return equalGenericTypes(type1, type2);
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

	public static Stream<Type> typeArguments(Type type) {
		if (type instanceof ParameterizedType) {
			return Arrays.stream(((ParameterizedType) type).getActualTypeArguments());
		} else {
			return Stream.empty();
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
		while (true) {
			int modifiers = clazz.getModifiers();
			if (clazz.isAnonymousClass() || clazz.isSynthetic()) {
				return true;
			} else if (isPublic(modifiers)) {
				return false;
			} else if (isPrivate(modifiers)) {
				return true;
			} else if (clazz.isArray()) {
				clazz = clazz.getComponentType();
			} else if (pkg == null || !pkg.equals(clazz.getPackage().getName())) {
				return true;
			} else if (clazz.getEnclosingClass() != null) {
				clazz = clazz.getEnclosingClass();
			} else {
				return false;
			}
		}
	}

	public static boolean isGeneric(Type type) {
		return !(type instanceof Class<?>);
	}

	public static boolean isGenericVariable(Type type) {
		return type instanceof TypeVariable<?>;
	}

	public static boolean isErasureHidden(Type type, String pkg) {
		if (type instanceof ParameterizedType) {
			Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
			if (actualTypeArguments == null) {
				return false;
			}
			for (Type typeArgument : actualTypeArguments) {
				if (isHidden(typeArgument, pkg)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isHidden(Constructor<?> constructor, String pkg) {
		int modifiers = constructor.getModifiers();
		if (isPrivate(modifiers)) {
			return true;
		} else {
			return isHidden(constructor.getDeclaringClass(), pkg)
				|| (constructor.getDeclaringClass().getEnclosingClass() != null && !isPublic(modifiers));
		}
	}

	public static boolean isHidden(Method method, String pkg) {
		int modifiers = method.getModifiers();
		if (isPrivate(modifiers)) {
			return true;
		} else {
			return isHidden(method.getDeclaringClass(), pkg)
				|| (method.getDeclaringClass().getEnclosingClass() != null && !isPublic(modifiers));
		}
	}

	public static boolean isBoxedPrimitive(Type type) {
		if (!(type instanceof Class<?>)) {
			return false;
		}
		Class<?> clazz = (Class<?>) type;
		if (clazz == Boolean.class) {
			return true;
		} else if (clazz == Character.class) {
			return true;
		} else if (clazz == Byte.class) {
			return true;
		} else if (clazz == Short.class) {
			return true;
		} else if (clazz == Integer.class) {
			return true;
		} else if (clazz == Float.class) {
			return true;
		} else if (clazz == Long.class) {
			return true;
		} else if (clazz == Double.class) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPrimitive(Type type) {
		return type instanceof Class<?>
			&& ((Class<?>) type).isPrimitive();
	}

	public static boolean isLiteral(Type type) {
		return isPrimitive(type)
			|| isBoxedPrimitive(type)
			|| type == String.class;
	}

	public static boolean isArray(Type type) {
		return type instanceof Class<?> && ((Class<?>) type).isArray()
			|| type instanceof GenericArrayType;
	}

	public static boolean isBound(Type type) {
		return !(type instanceof TypeVariable<?>)
			&& !(type instanceof WildcardType);
	}

	public static Type array(Type componentType) {
		if (componentType instanceof Class<?>) {
			return Array.newInstance((Class<?>) componentType, 0).getClass();
		} else {
			return genericArray(componentType);
		}
	}

	public static SerializableGenericArrayType genericArray(Type componentType) {
		return new SerializableGenericArrayType(serializableOf(componentType));
	}

	public static ParameterizedType parameterized(Type raw, Type owner, Type... typeArgs) {
		return new SerializableParameterizedType(serializableOf(raw), serializableOf(owner), serializableOf(typeArgs));
	}

	public static WildcardType wildcard() {
		return new SerializableWildcardType(NO_TYPES, NO_TYPES);
	}

	public static WildcardType wildcard(Type[] upperBounds, Type[] lowerBounds) {
		return new SerializableWildcardType(serializableOf(upperBounds), serializableOf(lowerBounds));
	}

	public static WildcardType wildcardExtends(Type... bounds) {
		return new SerializableWildcardType(serializableOf(bounds), NO_TYPES);
	}

	public static WildcardType wildcardSuper(Type... bounds) {
		return new SerializableWildcardType(NO_TYPES, serializableOf(bounds));
	}

	public static <D extends GenericDeclaration> TypeVariable<D> typeVariable(String name, D genericDeclaration, Type... bounds) {
		return new SerializableTypeVariable<>(name, genericDeclaration, serializableOf(bounds));
	}

	public static List<Field> allFields(Class<?> clazz) {
		Class<?> current = clazz;
		List<Field> fields = new ArrayList<>();
		while (current != Object.class) {
			for (Field field : current.getDeclaredFields()) {
				if (!field.isSynthetic()) {
					fields.add(field);
				}
			}
			current = current.getSuperclass();
		}
		return fields;

	}

	public static List<Class<?>> innerClasses(Class<?> of) {
		return asList(of.getDeclaredClasses());
	}

	public static List<Method> allMethods(Class<?> clazz) {
		Class<?> current = clazz;
		List<Method> methods = new ArrayList<>();
		while (current != Object.class) {
			for (Method method : current.getDeclaredMethods()) {
				if (!method.isSynthetic()) {
					methods.add(method);
				}
			}
			current = current.getSuperclass();
		}
		return methods;
	}

	private static int estimatedInterfaceMethodCount(Class<?> clazz) {
		SortedSet<Method> methods = new TreeSet<>((m1, m2) -> {
			int compare = m1.getName().compareTo(m2.getName());
			if (compare == 0) {
				compare = m1.getParameterCount() - m2.getParameterCount();
			}
			return compare;
		});
		IdentityWorkSet<Class<?>> todo = new IdentityWorkSet<>();
		for (Class<?> intrface : clazz.getInterfaces()) {
			todo.add(intrface);
		}
		while (todo.hasMoreElements()) {
			Class<?> current = todo.remove();
			for (Method method : current.getMethods()) {
				methods.add(method);
			}
			for (Class<?> intrface : current.getInterfaces()) {
				todo.add(intrface);
			}
		}
		return methods.size();
	}

	public static <T> Constructor<T> getDeclaredConstructor(Class<T> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
		return clazz.getDeclaredConstructor(parameterTypes);
	}

	public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
		Class<?> current = clazz;
		while (current != Object.class) {
			try {
				Method method = current.getDeclaredMethod(name, parameterTypes);
				if (!method.isSynthetic()) {
					return method;
				}
				current = current.getSuperclass();
			} catch (NoSuchMethodException e) {
				current = current.getSuperclass();
			}
		}
		return current.getDeclaredMethod(name, parameterTypes);
	}

	public static List<Method> getDeclaredMethods(Class<?> clazz, String methodName) {
		List<Method> methods = new ArrayList<>();
		Class<?> current = clazz;
		while (current != Object.class) {
			for (Method method : current.getDeclaredMethods()) {
				if (method.getName().equals(methodName)) {
					methods.add(method);
				}
			}
			current = current.getSuperclass();
		}
		return methods;
	}

	public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
		Class<?> current = clazz;
		while (current != Object.class) {
			try {
				return current.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				current = current.getSuperclass();
			}
		}
		throw new NoSuchFieldException(name);
	}

	public static List<Field> getDeclaredFields(Class<?> clazz, String name) {
		List<Field> list = new ArrayList<>();
		Class<?> current = clazz;
		while (current != Object.class) {
			try {
				list.add(current.getDeclaredField(name));
				current = current.getSuperclass();
			} catch (NoSuchFieldException e) {
				current = current.getSuperclass();
			}
		}
		return list;
	}

	public static boolean needsCast(Type variableType, Type expressionType) {
		return !baseType(variableType).isAssignableFrom(baseType(expressionType))
			&& !boxingEquivalentTypes(variableType, expressionType);
	}

	public static boolean isFinal(Field field) {
		return (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
	}

	public static boolean isStatic(Field field) {
		return (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
	}

	public static boolean isUnhandledSynthetic(Field field) {
		String name = field.getName();
		if (!field.isSynthetic() && !name.contains(SYNTHETIC_INDICATOR)) {
			return false;
		}
		for (String prefix : HANDLED_SYNTHETIC_PREFIXES) {
			if (name.startsWith(prefix)) {
				return false;
			}
		}
		return true;
	}

	public static Class<?> classFrom(Class<?> clazz, ClassLoader loader) throws ClassNotFoundException {
		int arrayDimensions = 0;

		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
			arrayDimensions++;
		}

		Class<?> reloadedClazz = clazz.isPrimitive() ? clazz : loader.loadClass(clazz.getName());
		for (int i = 0; i < arrayDimensions; i++) {
			reloadedClazz = Array.newInstance(reloadedClazz, 0).getClass();
		}
		return reloadedClazz;
	}

	public static Class<?>[] parameterTypesFrom(Method method, ClassLoader loader) throws ClassNotFoundException {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?>[] reloadedParameterTypes = new Class<?>[parameterTypes.length];
		for (int i = 0; i < reloadedParameterTypes.length; i++) {
			reloadedParameterTypes[i] = classFrom(parameterTypes[i], loader);
		}
		return reloadedParameterTypes;
	}

	public static Class<?> returnTypeFrom(Method method, ClassLoader loader) throws ClassNotFoundException {
		Class<?> returnType = method.getReturnType();
		Class<?> reloadedReturnType = classFrom(returnType, loader);
		return reloadedReturnType;
	}

	public static Type[] serializableOf(Type[] types) {
		if (types == null) {
			return null;
		}
		Type[] serializableTypes = new Type[types.length];
		for (int i = 0; i < serializableTypes.length; i++) {
			serializableTypes[i] = serializableOf(types[i]);
		}
		return serializableTypes;
	}

	public static Type serializableOf(Type type) {
		if (type == null) {
			return null;
		}
		Type serializableType = serializables.get(type);
		if (serializableType == null) {
			serializableType = newSerializableType(type);
			serializables.put(type, serializableType);
		}
		return serializableType;
	}

	private static Type newSerializableType(Type type) {
		if (type instanceof WildcardType) {
			WildcardType wildcardtype = (WildcardType) type;
			return wildcard(wildcardtype.getUpperBounds(), wildcardtype.getLowerBounds());
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return parameterized(parameterizedType.getRawType(), parameterizedType.getOwnerType(), parameterizedType.getActualTypeArguments());
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			return genericArray(genericArrayType.getGenericComponentType());
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			return typeVariable(typeVariable.getName(), typeVariable.getGenericDeclaration(), typeVariable.getBounds());
		} else {
			return baseType(type);
		}
	}

}
