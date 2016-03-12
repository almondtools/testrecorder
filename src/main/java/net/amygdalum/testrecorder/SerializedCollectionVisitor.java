package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedSet;

public interface SerializedCollectionVisitor<T> extends SerializedValueVisitor<T> {

	T visitList(SerializedList value);

	T visitSet(SerializedSet value);

	T visitMap(SerializedMap value);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static <S> Class<SerializedCollectionVisitor<S>> extend(SerializedValueVisitor<S> visitor) {
		return (Class) SerializedCollectionVisitor.class;
	}
	
}
