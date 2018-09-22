package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedRole;
import net.amygdalum.testrecorder.types.SerializedValueType;

public class HintManager implements RoleVisitor<Stream<Object>> {

	private Map<AnnotatedElement, Set<Object>> hints;

	public HintManager() {
		hints = new HashMap<>();
	}

	public void addHint(AnnotatedElement role, Object hint) {
		hints.computeIfAbsent(role, key -> new HashSet<>())
			.add(hint);
	}

	public <T> Stream<T> fetch(Class<T> clazz, SerializedRole role) {
		return role.accept(this)
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}

	public <T> Stream<T> fetch(Class<T> clazz, AnnotatedElement element) {
		if (element instanceof Executable) {
			return fetchResultHints((Executable) element)
				.filter(clazz::isInstance)
				.map(clazz::cast);
		} else if (element instanceof Field) {
			return fetchFieldHints((Field) element)
				.filter(clazz::isInstance)
				.map(clazz::cast);
		} else if (element instanceof Class<?>) {
			return fetchTypeHints((Class<?>) element)
				.filter(clazz::isInstance)
				.map(clazz::cast);
		} else {
			return Stream.empty();
		}
	}

	public Stream<Object> fetchArgumentHints(Executable method, int index) {
		Stream<Object> argumentHints = Arrays.stream(method.getParameterAnnotations()[index]);
		Set<Object> methodHints = hints.get(method);
		if (methodHints == null) {
			return argumentHints;
		}
		Stream<Object> customHints = methodHints.stream()
			.filter(obj -> obj.getClass().isArray())
			.map(obj -> (Object[]) obj)
			.map(arguments -> arguments[index]);
		return Stream.concat(argumentHints, customHints);
	}

	public Stream<Object> fetchResultHints(Executable method) {
		Stream<Object> resultHints = Arrays.stream(method.getAnnotations());
		Set<Object> methodHints = hints.get(method);
		if (methodHints == null) {
			return resultHints;
		}
		Stream<Object> customHints = methodHints.stream()
			.filter(obj -> !obj.getClass().isArray());
		return Stream.concat(resultHints, customHints);
	}

	public Stream<Object> fetchFieldHints(Field field) {
		Stream<Object> fieldHints = Arrays.stream(field.getAnnotations());
		Set<Object> fieldCustomHints = hints.get(field);
		if (fieldCustomHints == null) {
			return fieldHints;
		}
		Stream<Object> customHints = fieldCustomHints.stream();
		return Stream.concat(fieldHints, customHints);
	}

	public Stream<Object> fetchTypeHints(Class<?> type) {
		if (type == null) {
			return Stream.empty();
		}
		Stream<Object> typeHints = Arrays.stream(type.getAnnotations());
		Set<Object> typeCustomHints = hints.get(type);
		if (typeCustomHints == null) {
			return typeHints;
		}
		Stream<Object> customHints = typeCustomHints.stream();
		return Stream.concat(typeHints, customHints);
	}

	@Override
	public Stream<Object> visitArgument(SerializedArgument argument) {
		try {
			return fetchArgumentHints(argument.getSignature().resolveMethod(), argument.getIndex());
		} catch (NoSuchMethodException e) {
			return Stream.empty();
		}
	}

	@Override
	public Stream<Object> visitResult(SerializedResult result) {
		try {
			return fetchResultHints(result.getSignature().resolveMethod());
		} catch (NoSuchMethodException e) {
			return Stream.empty();
		}
	}

	@Override
	public Stream<Object> visitField(SerializedField field) {
		try {
			return fetchFieldHints(field.getSignature().resolveField());
		} catch (NoSuchFieldException e) {
			return Stream.empty();
		}
	}

	@Override
	public Stream<Object> visitReferenceType(SerializedReferenceType value) {
		return fetchTypeHints(value.getType());
	}

	@Override
	public Stream<Object> visitImmutableType(SerializedImmutableType value) {
		return fetchTypeHints(value.getType());
	}

	@Override
	public Stream<Object> visitValueType(SerializedValueType value) {
		return fetchTypeHints(value.getType());
	}

}
