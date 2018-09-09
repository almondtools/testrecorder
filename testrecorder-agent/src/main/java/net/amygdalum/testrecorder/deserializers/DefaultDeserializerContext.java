package net.amygdalum.testrecorder.deserializers;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.boxingEquivalentTypes;
import static net.amygdalum.testrecorder.util.Types.isGeneric;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.runtime.Wrapped;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedRole;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;

public class DefaultDeserializerContext implements DeserializerContext {

	private static final SerializedReferenceType GLOBAL_REFERRER = new GlobalRoot();

	private Map<SerializedValue, Set<SerializedReferenceType>> backReferences;
	private Map<SerializedValue, Set<SerializedValue>> closures;
	private Map<SerializedRole, Set<Object>> hints;
	private LocalVariableNameGenerator locals;
	private TypeManager types;
	private Map<SerializedValue, LocalVariable> defined;
	private Set<SerializedValue> computed;
	private Deque<SerializedRole> stack;

	public DefaultDeserializerContext(TypeManager types, LocalVariableNameGenerator locals) {
		this(new IdentityHashMap<>(), new IdentityHashMap<>(), new IdentityHashMap<>(), types, locals);
	}

	public DefaultDeserializerContext() {
		this.backReferences = new IdentityHashMap<>();
		this.closures = new IdentityHashMap<>();
		this.hints = new IdentityHashMap<>();
		this.types = new DeserializerTypeManager();
		this.locals = new LocalVariableNameGenerator();
		this.defined = new IdentityHashMap<>();
		this.computed = new HashSet<>();
		this.stack = new LinkedList<>();
	}

	private DefaultDeserializerContext(Map<SerializedValue, Set<SerializedReferenceType>> backReferences, Map<SerializedValue, Set<SerializedValue>> closures,
		Map<SerializedRole, Set<Object>> hints, TypeManager types, LocalVariableNameGenerator locals) {
		this.backReferences = backReferences;
		this.closures = closures;
		this.hints = hints;
		this.types = types;
		this.locals = locals;
		this.defined = new IdentityHashMap<>();
		this.computed = new HashSet<>();
		this.stack = new LinkedList<>();
	}

	public static DefaultDeserializerContext empty() {
		return new DefaultDeserializerContext();
	}

	@Override
	public LocalVariableNameGenerator getLocals() {
		return locals;
	}

	@Override
	public TypeManager getTypes() {
		return types;
	}

	@Override
	public String temporaryLocal() {
		return locals.fetchName("temp");
	}

	@Override
	public String newLocal(String name) {
		return locals.fetchName(name);
	}

	@Override
	public LocalVariable localVariable(SerializedValue value, Type type) {
		String name = locals.fetchName(type);
		LocalVariable definition = new LocalVariable(name, type);
		defined.put(value, definition);
		return definition;
	}

	@Override
	public void finishVariable(SerializedValue value) {
		defined.computeIfPresent(value, (val, def) -> def.finish());
	}

	@Override
	public void resetVariable(SerializedValue value) {
		LocalVariable variable = defined.remove(value);
		locals.freeName(variable.getName());
	}

	@Override
	public boolean defines(SerializedValue value) {
		return defined.containsKey(value);
	}

	@Override
	public LocalVariable getDefinition(SerializedValue value) {
		return defined.get(value);
	}

	@Override
	public <T> DefaultDeserializerContext newIsolatedContext(TypeManager types, LocalVariableNameGenerator locals) {
		return new DefaultDeserializerContext(backReferences, closures, hints, types, locals);
	}

	@Override
	public void addHint(SerializedRole role, Object hint) {
		hints.computeIfAbsent(role, key -> new HashSet<>())
			.add(hint);
	}

	@Override
	public <T> Optional<T> getHint(SerializedRole role, Class<T> clazz) {
		Iterator<SerializedRole> iterator = stack.iterator();
		current: if (iterator.hasNext()) {
			SerializedRole current = iterator.next();
			if (current != role) {
				break current;
			}
			Optional<T> annotation = Arrays.stream(current.getAnnotations())
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.findFirst();
			if (annotation.isPresent()) {
				return annotation;
			}
		}
		parent: if (iterator.hasNext()) {
			SerializedRole current = iterator.next();
			if (current instanceof SerializedValue) {
				break parent;
			}
			Optional<T> annotation = Arrays.stream(current.getAnnotations())
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.findFirst();
			if (annotation.isPresent()) {
				return annotation;
			}
		}
		return hints.getOrDefault(role, emptySet()).stream()
			.filter(clazz::isInstance)
			.map(clazz::cast)
			.findFirst();
	}

	@Override
	public <T> Stream<T> getHints(SerializedRole role, Class<T> clazz) {
		Builder<T> allhints = Stream.builder();
		Iterator<SerializedRole> iterator = stack.iterator();
		current: if (iterator.hasNext()) {
			SerializedRole current = iterator.next();
			if (current != role) {
				break current;
			}
			Arrays.stream(current.getAnnotations())
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.forEach(allhints::add);
		}
		parent: if (iterator.hasNext()) {
			SerializedRole current = iterator.next();
			if (current instanceof SerializedValue) {
				break parent;
			}
			Arrays.stream(current.getAnnotations())
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.forEach(allhints::add);
		}
		hints.getOrDefault(role, emptySet()).stream()
			.filter(clazz::isInstance)
			.map(clazz::cast)
			.forEach(allhints::add);

		return allhints.build();
	}

	@Override
	public int refCount(SerializedValue value) {
		int size = 0;
		Set<SerializedReferenceType> references = backReferences.get(value);
		if (references != null) {
			size += references.size();
		}
		return size;
	}

	@Override
	public void ref(SerializedReferenceType value, SerializedValue referencedValue) {
		backReferences.computeIfAbsent(referencedValue, key -> new HashSet<>())
			.add(value);
	}

	@Override
	public void staticRef(SerializedValue referencedValue) {
		backReferences.computeIfAbsent(referencedValue, key -> new HashSet<>())
			.add(GLOBAL_REFERRER);
	}

	@Override
	public Set<SerializedValue> closureOf(SerializedValue value) {
		Set<SerializedValue> closure = closures.get(value);
		if (closure != null) {
			return closure;
		}
		if (value instanceof SerializedImmutableType) {
			return Collections.singleton(value);
		} else if (value instanceof SerializedReferenceType) {
			closures.put(value, Collections.singleton(value));

			closure = new HashSet<>();
			closure.add(value);
			value.referencedValues().stream()
				.flatMap(val -> closureOf(val).stream())
				.forEach(closure::add);

			closures.put(value, closure);
			return closure;
		} else {
			return Collections.singleton(value);
		}
	}

	@Override
	public String adapt(String expression, Type resultType, Type type) {
		if (needsUnwrapping(resultType, type)) {
			if (types.isHidden(resultType) || baseType(resultType) == Object.class) {
				type = Object.class;
				expression = callMethod(expression, "value");
			} else {
				type = baseType(resultType);
				expression = callMethod(expression, "value", types.getRawClass(type));
			}
		}
		if ((!assignableTypes(resultType, type) || types.isHidden(type))
			&& !boxingEquivalentTypes(resultType, type)
			&& baseType(resultType) != Wrapped.class) {
			if (isGeneric(resultType) && isGeneric(type)) {
				expression = cast(types.getRawTypeName(resultType), expression);
			}
			expression = cast(types.getVariableTypeName(resultType), expression);
		}
		return expression;
	}

	private boolean needsUnwrapping(Type resultType, Type type) {
		return (baseType(resultType) != Wrapped.class && type == Wrapped.class)
			|| (baseType(resultType) != Wrapped.class && types.isHidden(type));
	}

	@Override
	public boolean needsAdaptation(Type resultType, Type type) {
		if (resultType == null || type == null) {
			return false;
		} else if (baseType(resultType) != Wrapped.class && type == Wrapped.class) {
			return true;
		} else if (baseType(resultType) != Wrapped.class && types.isHidden(type)) {
			return true;
		}
		if ((!assignableTypes(resultType, type) || types.isHidden(type))
			&& !boxingEquivalentTypes(resultType, type)
			&& baseType(resultType) != Wrapped.class) {
			return true;
		}
		return false;
	}

	@Override
	public Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation) {
		LocalVariable local = localVariable(value, type);
		try {
			Computation definition = computation.define(local);
			finishVariable(value);
			return definition;
		} catch (DeserializationException e) {
			resetVariable(value);
			throw e;
		}
	}

	@Override
	public boolean isComputed(SerializedValue value) {
		boolean changed = computed.add(value);
		return !changed;
	}

	@Override
	public Optional<SerializedValue> resolve(int id) {
		return defined.keySet().stream()
			.filter(value -> (value instanceof SerializedReferenceType) && ((SerializedReferenceType) value).getId() == id)
			.findFirst();
	}

	@Override
	public <T extends SerializedRole, S> S withRole(T role, Function<T, S> continuation) {
		try {
			stack.push(role);
			return continuation.apply(role);
		} finally {
			stack.pop();
		}
	}

	private static class GlobalRoot implements SerializedReferenceType {

		GlobalRoot() {
		}

		@Override
		public <T> T accept(RoleVisitor<T> visitor) {
			return null;
		}

		@Override
		public <T> T accept(ReferenceTypeVisitor<T> visitor) {
			return null;
		}

		@Override
		public void useAs(Type resultType) {
		}

		@Override
		public Type[] getUsedTypes() {
			return new Type[] { Class.class };
		}

		@Override
		public Class<?> getType() {
			return Class.class;
		}

		@Override
		public List<SerializedValue> referencedValues() {
			return emptyList();
		}

		@Override
		public Annotation[] getAnnotations() {
			return NO_ANNOTATIONS;
		}

		@Override
		public void setId(int id) {
		}

		@Override
		public int getId() {
			return 0;
		}

	}

}
