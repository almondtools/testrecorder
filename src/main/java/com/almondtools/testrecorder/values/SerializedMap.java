package com.almondtools.testrecorder.values;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.almondtools.testrecorder.SerializedCollectionVisitor;
import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedMap implements SerializedValue, Map<SerializedValue, SerializedValue> {

	private Type type;
	private Map<SerializedValue, SerializedValue> map;

	public SerializedMap(Type type) {
		this.type = type;
		map = new LinkedHashMap<>();
	}
	
	@Override
	public Type getType() {
		return type;
	}

	public Type getKeyType() {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[0];
		} else {
			return Object.class;
		}
	}

	public Type getValueType() {
		if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).getActualTypeArguments()[1];
		} else {
			return Object.class;
		}
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.as(SerializedCollectionVisitor.extend(visitor))
			.map(v -> v.visitMap(this))
			.orElseGet(() -> visitor.visitUnknown(this));
	}
	
	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public SerializedValue get(Object key) {
		return map.get(key);
	}

	public SerializedValue put(SerializedValue key, SerializedValue value) {
		return map.put(key, value);
	}

	public SerializedValue remove(Object key) {
		return map.remove(key);
	}

	public void putAll(Map<? extends SerializedValue, ? extends SerializedValue> m) {
		map.putAll(m);
	}

	public void clear() {
		map.clear();
	}

	public Set<SerializedValue> keySet() {
		return map.keySet();
	}

	public Collection<SerializedValue> values() {
		return map.values();
	}

	public Set<java.util.Map.Entry<SerializedValue, SerializedValue>> entrySet() {
		return map.entrySet();
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

}
