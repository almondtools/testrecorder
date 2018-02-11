package net.amygdalum.testrecorder.types;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;


public interface DeserializerContext {

	DeserializerContext getParent();

	<T> DeserializerContext newWithHints(T[] hints);

	<T> Optional<T> getHint(Class<T> clazz);

	<T> Stream<T> getHints(Class<T> clazz);

	int refCount(SerializedValue value);

	void ref(SerializedReferenceType value, SerializedValue referencedValue);

	void staticRef(SerializedValue referencedValue);

	Set<SerializedValue> closureOf(SerializedValue value);

}