package net.amygdalum.testrecorder.deserializers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;

public class DefaultDeserializerContext implements DeserializerContext {

	public static final DefaultDeserializerContext NULL = new DefaultDeserializerContext();

	private static final SerializedReferenceType GLOBAL_REFERRER = new GlobalRoot();

	private DefaultDeserializerContext parent;
	private Map<SerializedValue, Set<SerializedReferenceType>> backReferences;
	private Map<SerializedValue, Set<SerializedValue>> closures;
	private List<Object> hints;

	public DefaultDeserializerContext() {
		this.backReferences = new IdentityHashMap<>();
		this.closures = new IdentityHashMap<>();
		this.hints = emptyList();
	}

	private DefaultDeserializerContext(DefaultDeserializerContext parent, Collection<Object> hints) {
		this.parent = parent;
		this.backReferences = parent.backReferences;
		this.closures = parent.closures;
		this.hints = new ArrayList<>(hints);
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

	private static class GlobalRoot implements SerializedReferenceType {

		public GlobalRoot() {
		}

		@Override
		public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
			return null;
		}

		@Override
		public void setResultType(Type resultType) {
		}

		@Override
		public Type getResultType() {
			return Class.class;
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
