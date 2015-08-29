package com.almondtools.invivoderived;

import com.almondtools.invivoderived.values.SerializedList;
import com.almondtools.invivoderived.values.SerializedMap;
import com.almondtools.invivoderived.values.SerializedSet;

public interface SerializedCollectionVisitor<T> extends SerializedValueVisitor<T> {

	T visitList(SerializedList value);

	T visitSet(SerializedSet value);

	T visitMap(SerializedMap value);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static <S> Class<SerializedCollectionVisitor<S>> extend(SerializedValueVisitor<S> visitor) {
		return (Class) SerializedCollectionVisitor.class;
	}
	
}
