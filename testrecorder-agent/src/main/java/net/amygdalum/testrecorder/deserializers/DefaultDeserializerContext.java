package net.amygdalum.testrecorder.deserializers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.callMethod;
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.boxingEquivalentTypes;
import static net.amygdalum.testrecorder.util.Types.isGeneric;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.runtime.Wrapped;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;

public class DefaultDeserializerContext implements DeserializerContext {

	private static final SerializedReferenceType GLOBAL_REFERRER = new GlobalRoot();

	private DefaultDeserializerContext parent;
	private Map<SerializedValue, Set<SerializedReferenceType>> backReferences;
	private Map<SerializedValue, Set<SerializedValue>> closures;
	private List<Object> hints;
	private LocalVariableNameGenerator locals;
	private TypeManager types;
	private Map<SerializedValue, LocalVariable> defined;
	private Set<SerializedValue> computed;

	public DefaultDeserializerContext() {
		this.backReferences = new IdentityHashMap<>();
		this.closures = new IdentityHashMap<>();
		this.hints = emptyList();
		this.types = new DeserializerTypeManager();
		this.locals = new LocalVariableNameGenerator();
		this.defined = new IdentityHashMap<>();
		this.computed = new HashSet<>();
	}

	public DefaultDeserializerContext(TypeManager types, LocalVariableNameGenerator locals) {
		this.backReferences = new IdentityHashMap<>();
		this.closures = new IdentityHashMap<>();
		this.hints = emptyList();
		this.types = types;
		this.locals = locals;
		this.defined = new IdentityHashMap<>();
		this.computed = new HashSet<>();
	}

	private DefaultDeserializerContext(DefaultDeserializerContext parent, Collection<Object> hints) {
		this.parent = parent;
		this.backReferences = parent.backReferences;
		this.closures = parent.closures;
		this.hints = new ArrayList<>(hints);
		this.types = parent.types;
		this.locals = parent.locals;
		this.defined = parent.defined;
		this.computed = parent.computed;
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
	public LocalVariable localVariable(SerializedValue value) {
		Type type = types.isHidden(value.getType())
			? types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class)
			: value.getType();
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
		defined.remove(value);
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
	public DefaultDeserializerContext getParent() {
		return parent;
	}

	@Override
	public <T> DefaultDeserializerContext newWithHints(T[] hints) {
		return new DefaultDeserializerContext(this, asList(hints));
	}

	@Override
	public <T> Optional<T> getHint(Class<T> clazz) {
		return hints.stream()
			.filter(hint -> clazz.isInstance(hint))
			.map(hint -> clazz.cast(hint))
			.findFirst();
	}

	@Override
	public <T> Stream<T> getHints(Class<T> clazz) {
		return hints.stream()
			.filter(hint -> clazz.isInstance(hint))
			.map(hint -> clazz.cast(hint));
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
		backReferences.compute(referencedValue, (referenced, backreferenced) -> {
			if (backreferenced == null) {
				backreferenced = new HashSet<>();
			}
			backreferenced.add(value);
			return backreferenced;
		});
	}

	@Override
	public void staticRef(SerializedValue referencedValue) {
		backReferences.compute(referencedValue, (referenced, backreferenced) -> {
			if (backreferenced == null) {
				backreferenced = new HashSet<>();
			}
			backreferenced.add(GLOBAL_REFERRER);
			return backreferenced;
		});
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
		if (baseType(resultType) != Wrapped.class && type == Wrapped.class) {
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
	public Computation forVariable(SerializedValue value, LocalVariableDefinition computation) {
		LocalVariable local = localVariable(value);
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

	private static class GlobalRoot implements SerializedReferenceType {

		public GlobalRoot() {
		}

		@Override
		public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
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
		public void setType(Type type) {
		}

		@Override
		public Type getType() {
			return Class.class;
		}

		@Override
		public List<SerializedValue> referencedValues() {
			return emptyList();
		}

		@Override
		public Annotation[] getAnnotations() {
			return new Annotation[0];
		}

		@Override
		public <T extends Annotation> Optional<T> getAnnotation(Class<T> clazz) {
			return Optional.empty();
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
