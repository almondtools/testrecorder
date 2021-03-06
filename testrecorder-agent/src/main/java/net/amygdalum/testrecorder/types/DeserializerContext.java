package net.amygdalum.testrecorder.types;

import static java.util.Collections.emptySet;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public interface DeserializerContext {

	DeserializerContext NULL = new DeserializerContext() {

		@Override
		public void clear() {
		}
		
		@Override
		public <T> DeserializerContext newIsolatedContext(TypeManager types, LocalVariableNameGenerator locals) {
			return NULL;
		}

		@Override
		public void addHint(AnnotatedElement role, Object hint) {
		}

		@Override
		public <T> Optional<T> getHint(SerializedRole role, Class<T> clazz) {
			return Optional.empty();
		}

		@Override
		public <T> Optional<T> getHint(AnnotatedElement element, Class<T> clazz) {
			return Optional.empty();
		}

		@Override
		public <T> Stream<T> getHints(SerializedRole role, Class<T> clazz) {
			return Stream.empty();
		}

		@Override
		public <T> Stream<T> getHints(AnnotatedElement element, Class<T> clazz) {
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
		public Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation) {
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
		public LocalVariable localVariable(SerializedValue value, Type type) {
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

		@Override
		public <T extends SerializedRole, S> S withRole(T role, Function<T, S> continuation) {
			return continuation.apply(role);
		}

	};
	
	void clear();

	<T> DeserializerContext newIsolatedContext(TypeManager types, LocalVariableNameGenerator locals);

	void addHint(AnnotatedElement role, Object hint);

	<T> Optional<T> getHint(SerializedRole role, Class<T> clazz);

	<T> Optional<T> getHint(AnnotatedElement element, Class<T> clazz);

	<T> Stream<T> getHints(SerializedRole role, Class<T> clazz);

	<T> Stream<T> getHints(AnnotatedElement element, Class<T> clazz);
	
	int refCount(SerializedValue value);

	void ref(SerializedReferenceType value, SerializedValue referencedValue);

	void staticRef(SerializedValue referencedValue);

	Set<SerializedValue> closureOf(SerializedValue value);

	TypeManager getTypes();

	String adapt(String expression, Type resultType, Type type);

	boolean defines(SerializedValue value);

	LocalVariable getDefinition(SerializedValue value);

	boolean needsAdaptation(Type resultType, Type type);

	Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation);

	String temporaryLocal();

	String newLocal(String name);

	LocalVariable localVariable(SerializedValue value, Type type);

	void resetVariable(SerializedValue value);

	void finishVariable(SerializedValue value);

	LocalVariableNameGenerator getLocals();

	boolean isComputed(SerializedValue value);

	Optional<SerializedValue> resolve(int id);

	<T extends SerializedRole,S> S withRole(T role, Function<T, S> continuation);

}