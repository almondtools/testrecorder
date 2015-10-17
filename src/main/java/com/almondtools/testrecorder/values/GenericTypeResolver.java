package com.almondtools.testrecorder.values;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashSet;
import java.util.Set;

public final class GenericTypeResolver {

	private GenericTypeResolver() {
	}

	public static Type resolve(Set<Type> allTypes, Type type) {
		while (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			Object decl = typeVariable.getGenericDeclaration();
			if (!(decl instanceof Class)) {
				return Object.class;
			}
			Class<?> clazz = (Class<?>) decl;
			int pos = asList(clazz.getTypeParameters()).indexOf(typeVariable);
			type = allTypes.stream()
				.filter(cand -> cand instanceof ParameterizedType)
				.map(cand -> (ParameterizedType) cand)
				.filter(cand -> cand.getRawType() == decl)
				.findFirst()
				.map(cand -> cand.getActualTypeArguments()[pos])
				.orElse(Object.class);
		}
		return type;
	}

	public static Set<Type> findAllTypes(Type type) {
		return findAllTypes(type, new LinkedHashSet<>());
	}

	public static Set<Type> findAllTypes(Type type, Set<Type> all) {
		if (type == null) {
			return all;
		}
		boolean changed = all.add(type);
		if (!changed) {
			return all;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return findAllTypes(parameterizedType.getRawType(), all);
		} else if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			for (Type boundType : typeVariable.getBounds()) {
				findAllTypes(boundType, all);
			}
			return all;
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			if (wildcardType.getUpperBounds() != null) {
				for (Type boundType : wildcardType.getUpperBounds()) {
					findAllTypes(boundType, all);
				}
			}
			return all;
		} else if (type instanceof GenericArrayType) {
			return findAllTypes(Array.class, all);
		} else if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			for (Type interfaceType : clazz.getGenericInterfaces()) {
				findAllTypes(interfaceType, all);
			}
			return findAllTypes(clazz.getGenericSuperclass(), all);
		} else {
			return all;
		}
	}

}
