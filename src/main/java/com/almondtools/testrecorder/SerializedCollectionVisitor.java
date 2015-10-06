package com.almondtools.testrecorder;

import com.almondtools.testrecorder.values.SerializedList;
import com.almondtools.testrecorder.values.SerializedMap;
import com.almondtools.testrecorder.values.SerializedSet;

public interface SerializedCollectionVisitor<T> extends SerializedValueVisitor<T> {

	T visitList(SerializedList value);

	T visitSet(SerializedSet value);

	T visitMap(SerializedMap value);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static <S> Class<SerializedCollectionVisitor<S>> extend(SerializedValueVisitor<S> visitor) {
		return (Class) SerializedCollectionVisitor.class;
	}
	
}
