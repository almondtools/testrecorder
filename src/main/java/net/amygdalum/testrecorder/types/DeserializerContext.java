package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.LocalVariable;
import net.amygdalum.testrecorder.deserializers.LocalVariableDefinition;
import net.amygdalum.testrecorder.deserializers.LocalVariableNameGenerator;
import net.amygdalum.testrecorder.deserializers.TypeManager;


public interface DeserializerContext {

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

	Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation);

	String temporaryLocal();

	String newLocal(String name);

	LocalVariable localVariable(SerializedValue value, Type type);

	void resetVariable(SerializedValue value);

	void finishVariable(SerializedValue value);

	LocalVariableNameGenerator getLocals();

	boolean isComputed(SerializedValue value);

	Optional<SerializedValue> resolve(int id);

	
}