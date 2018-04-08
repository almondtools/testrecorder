package net.amygdalum.testrecorder.types;

import static java.util.Collections.emptySet;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface DeserializerContext {

	public static final DeserializerContext NULL = new DeserializerContext() {

		@Override
		public DeserializerContext getParent() {
			return null;
		}

		@Override
		public <T> DeserializerContext newWithHints(T[] hints) {
			return NULL;
		}

		@Override
		public <T> Optional<T> getHint(Class<T> clazz) {
			return Optional.empty();
		}

		@Override
		public <T> Stream<T> getHints(Class<T> clazz) {
			return Stream.empty();
		}

		@Override
		public int refCount(SerializedValue value) {
			return 0;
		}

		@Override
		public void ref(SerializedReferenceType value, SerializedValue referencedValue) {
		}

		@Override
		public void staticRef(SerializedValue referencedValue) {
		}

		@Override
		public Set<SerializedValue> closureOf(SerializedValue value) {
			return emptySet();
		}

		@Override
		public TypeManager getTypes() {
			return null;
		}

		@Override
		public String adapt(String expression, Type resultType, Type type) {
			return null;
		}

		@Override
		public boolean defines(SerializedValue value) {
			return false;
		}

		@Override
		public LocalVariable getDefinition(SerializedValue value) {
			return null;
		}

		@Override
		public boolean needsAdaptation(Type resultType, Type type) {
			return false;
		}

		@Override
		public Computation forVariable(SerializedValue value, LocalVariableDefinition computation) {
			return null;
		}

		@Override
		public String temporaryLocal() {
			return null;
		}

		@Override
		public String newLocal(String name) {
			return null;
		}

		@Override
		public LocalVariable localVariable(SerializedValue value) {
			return null;
		}

		@Override
		public void resetVariable(SerializedValue value) {
		}

		@Override
		public void finishVariable(SerializedValue value) {
		}

		@Override
		public LocalVariableNameGenerator getLocals() {
			return null;
		}

		@Override
		public boolean isComputed(SerializedValue value) {
			return false;
		}

		@Override
		public Optional<SerializedValue> resolve(int id) {
			return Optional.empty();
		}

	};

	DeserializerContext getParent();

	<T> DeserializerContext newWithHints(T[] hints);

	<T> Optional<T> getHint(Class<T> clazz);

	<T> Stream<T> getHints(Class<T> clazz);

	int refCount(SerializedValue value);

	void ref(SerializedReferenceType value, SerializedValue referencedValue);

	void staticRef(SerializedValue referencedValue);

	Set<SerializedValue> closureOf(SerializedValue value);

	TypeManager getTypes();

	String adapt(String expression, Type resultType, Type type);

	boolean defines(SerializedValue value);

	LocalVariable getDefinition(SerializedValue value);

	boolean needsAdaptation(Type resultType, Type type);

	Computation forVariable(SerializedValue value, LocalVariableDefinition computation);

	String temporaryLocal();

	String newLocal(String name);

	LocalVariable localVariable(SerializedValue value);

	void resetVariable(SerializedValue value);

	void finishVariable(SerializedValue value);

	LocalVariableNameGenerator getLocals();

	boolean isComputed(SerializedValue value);

	Optional<SerializedValue> resolve(int id);

}