package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.genericArray;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.serializableOf;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.amygdalum.testrecorder.util.SerializableWildcardType;

public class GenericsResolver {

	private Class<?>[] actualArgumentTypes;
	private Map<Type, Type> groundTypes;

	public GenericsResolver(Class<?>[] actualArgumentTypes) {
		this.actualArgumentTypes = actualArgumentTypes;
		this.groundTypes = new HashMap<>();
	}

	private void resolve(GenericDeclaration declaration) {
		if (!(declaration instanceof Method)) {
			return;
		}
		Method method = (Method) declaration;
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		unify(genericParameterTypes, actualArgumentTypes);

	}

	private void unify(Type[] types, Class<?>[] classes) {
		if (classes.length != types.length) {
			return;
		}
		int len = classes.length;
		Queue<Unify> toresolve = new LinkedList<>();
		for (int i = 0; i < len; i++) {
			if (groundTypes.containsKey(types[i])) {
				continue;
			}
			Unify candidate = new Unify(classes[i], types[i]);
			toresolve.add(candidate);
		}
		int lastresolvedVariables = groundTypes.size();
		int newresolvedVariables = groundTypes.size();
		List<Unify> nexts = new ArrayList<>();
		do {
			while (!toresolve.isEmpty()) {
				Unify current = toresolve.remove();
				Type type = current.type;
				Class<?> target = current.clazz;
				unify(bind(type), target);
				if (groundTypes.containsKey(type)) {
					continue;
				}
				nexts.add(current);
			}
			lastresolvedVariables = newresolvedVariables;
			newresolvedVariables = groundTypes.size();
			toresolve.addAll(nexts);
			nexts.clear();
		} while (newresolvedVariables > lastresolvedVariables);
	}

	private Type bind(Type type) {
		if (type instanceof Class<?>) {
			return type;
		}
		if (groundTypes.containsValue(type)) {
			return type;
		}

		Type ground = resolveType(type);
		if (ground == type) {
			return type;
		} else if (ground != null) {
			return ground;
		}

		if (type instanceof ParameterizedType) {
			return bindParameterized((ParameterizedType) type);
		} else if (type instanceof WildcardType) {
			return bindWildcard((WildcardType) type);
		} else {
			return type;
		}
	}

	private Type bindParameterized(ParameterizedType parameterizedType) {
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
		Type[] boundTypeArguments = bindArray(actualTypeArguments);
		if (actualTypeArguments == boundTypeArguments) {
			return parameterizedType;
		} else {
			ParameterizedType type = parameterized(parameterizedType.getRawType(), parameterizedType.getOwnerType(), boundTypeArguments);
			boolean ground = true;
			for (int i = 0; i < actualTypeArguments.length; i++) {
				ground &= groundTypes.containsKey(actualTypeArguments[i]);
			}
			if (ground) {
				assignGround(parameterizedType, type);
			}
			return type;
		}
	}

	private Type bindWildcard(WildcardType wildCardType) {
		Type[] actualUpperBounds = wildCardType.getUpperBounds();
		Type[] boundUpperBounds = bindArray(actualUpperBounds);
		Type[] actualLowerBounds = wildCardType.getLowerBounds();
		Type[] boundLowerBounds = bindArray(actualLowerBounds);
		if (actualUpperBounds == boundUpperBounds
			&& actualLowerBounds == boundLowerBounds) {
			return wildCardType;
		} else {
			SerializableWildcardType type = wildcard(boundUpperBounds, boundLowerBounds);
			boolean ground = true;
			for (int i = 0; i < actualUpperBounds.length; i++) {
				ground &= groundTypes.containsKey(actualUpperBounds[i]);
			}
			for (int i = 0; i < actualLowerBounds.length; i++) {
				ground &= groundTypes.containsKey(actualLowerBounds[i]);
			}
			if (ground) {
				assignGround(wildCardType, type);
			}
			return type;
		}
	}

	private Type[] bindArray(Type[] types) {
		Type[] resolvedTypes = null;
		for (int i = 0; i < types.length; i++) {
			Type unresolvedType = types[i];
			Type resolvedType = bind(unresolvedType);
			if (resolvedTypes != null) {
				resolvedTypes[i] = resolvedType;
			} else if (unresolvedType != resolvedType) {
				resolvedTypes = new Type[types.length];
				System.arraycopy(types, 0, resolvedTypes, 0, i);
				resolvedTypes[i] = resolvedType;
			}
		}
		if (resolvedTypes == null) {
			return types;
		} else {
			return resolvedTypes;
		}
	}

	private Type unify(Type type, Type target) {
		if (type == target) {
			return type;
		} else if (type instanceof Class<?>) {
			throw new TypeResolutionException("signatures are not unifiable");
		} else if (type instanceof TypeVariable<?>) {
			return unifyVariable((TypeVariable<?>) type, target);
		} else if (type instanceof ParameterizedType) {
			return unifyParameterized((ParameterizedType) type, target);
		} else if (type instanceof WildcardType) {
			return unifyWildcard((WildcardType) type, target);
		} else {
			throw new TypeResolutionException("signatures are not unifiable");
		}
	}

	private Type unifyParameterized(ParameterizedType parameterizedType, Type target) {
		if (assignableTypes(parameterizedType, target)) {
			return parameterizedType;
		}
		return target;
	}

	private Type unifyWildcard(WildcardType wildcardType, Type target) {
		for (Type bound : wildcardType.getUpperBounds()) {
			if (!assignableTypes(bound, target)) {
				return wildcardType;
			}
		}
		for (Type bound : wildcardType.getLowerBounds()) {
			if (!assignableTypes(target, bound)) {
				return wildcardType;
			}
		}
		return target;
	}

	private Type unifyVariable(TypeVariable<?> typeVariable, Type target) {
		assignGround(typeVariable, target);
		return target;
	}

	private void assignGround(Type type, Type target) {
		groundTypes.put(serializableOf(type), target);
	}

	private Type resolveType(Type type) {
		return groundTypes.get(serializableOf(type));
	}

	private Type resolveType(Type type, Type defaultType) {
		return groundTypes.getOrDefault(serializableOf(type), defaultType);
	}
	
	public Type resolve(Type type) {
		if (type instanceof Class<?>) {
			return type;
		}
		Type resolvedType = resolveType(type);
		if (resolvedType != null) {
			return resolvedType;
		}

		resolvedType = resolveParametric(type);
		
		return resolvedType;
	}

	private Type resolveParametric(Type type) {
		if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Type unresolvedComponentType = genericArrayType.getGenericComponentType();
			Type resolvedComponentType = resolve(unresolvedComponentType);
			if (unresolvedComponentType == resolvedComponentType) {
				return type;
			} else {
				return genericArray(resolvedComponentType);
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] unresolvedTypeArguments = parameterizedType.getActualTypeArguments();
			Type[] resolvedTypeArguments = resolve(unresolvedTypeArguments);
			if (unresolvedTypeArguments == resolvedTypeArguments) {
				return type;
			} else {
				return parameterized(parameterizedType.getRawType(), parameterizedType.getOwnerType(), resolvedTypeArguments);
			}
		} else if (type instanceof TypeVariable<?>) {
			@SuppressWarnings("unchecked")
			TypeVariable<GenericDeclaration> typeVariable = (TypeVariable<GenericDeclaration>) type;
			resolve(typeVariable.getGenericDeclaration());

			return resolveType(typeVariable, type);
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] unresolvedLowerBounds = wildcardType.getLowerBounds();
			Type[] resolvedLowerBounds = resolve(unresolvedLowerBounds);
			Type[] unresolvedUpperBounds = wildcardType.getUpperBounds();
			Type[] resolvedUpperBounds = resolve(unresolvedUpperBounds);
			if (unresolvedLowerBounds == resolvedLowerBounds
				&& unresolvedUpperBounds == resolvedUpperBounds) {
				return type;
			} else {
				return wildcard(resolvedUpperBounds, resolvedLowerBounds);
			}
		} else {
			return type;
		}
	}

	public Type[] resolve(Type[] types) {
		Type[] resolvedTypes = null;
		for (int i = 0; i < types.length; i++) {
			Type unresolvedType = types[i];
			Type resolvedType = resolve(unresolvedType);
			if (resolvedTypes != null) {
				resolvedTypes[i] = resolvedType;
			} else if (unresolvedType != resolvedType) {
				resolvedTypes = new Type[types.length];
				System.arraycopy(types, 0, resolvedTypes, 0, i);
				resolvedTypes[i] = resolvedType;
			}
		}
		if (resolvedTypes == null) {
			return types;
		} else {
			return resolvedTypes;
		}
	}

	private static class Unify {
		public Class<?> clazz;
		public Type type;

		public Unify(Class<?> clazz, Type type) {
			this.clazz = clazz;
			this.type = type;
		}
	}
}
