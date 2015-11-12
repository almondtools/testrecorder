package com.almondtools.testrecorder.visitors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.almondtools.testrecorder.DeserializationException;
import com.almondtools.testrecorder.SerializedCollectionVisitor;
import com.almondtools.testrecorder.SerializedImmutableVisitor;
import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.util.GenericObject;
import com.almondtools.testrecorder.util.GenericObjectException;
import com.almondtools.testrecorder.values.SerializedArray;
import com.almondtools.testrecorder.values.SerializedBigDecimal;
import com.almondtools.testrecorder.values.SerializedBigInteger;
import com.almondtools.testrecorder.values.SerializedField;
import com.almondtools.testrecorder.values.SerializedList;
import com.almondtools.testrecorder.values.SerializedLiteral;
import com.almondtools.testrecorder.values.SerializedMap;
import com.almondtools.testrecorder.values.SerializedNull;
import com.almondtools.testrecorder.values.SerializedObject;
import com.almondtools.testrecorder.values.SerializedSet;

public class Deserializer implements SerializedValueVisitor<Object>, SerializedCollectionVisitor<Object>, SerializedImmutableVisitor<Object> {

	private Map<SerializedValue, Object> deserialized;
	
	public Deserializer() {
		this.deserialized = new IdentityHashMap<>();
	}

	@SuppressWarnings("unchecked")
	private <T> T fetch(SerializedValue key, Supplier<T> supplier) {
		return (T) deserialized.computeIfAbsent(key, k -> supplier.get());
	}
	
	@Override
	public Object visitField(SerializedField field) {
		throw new DeserializationException(field.toString());
	}

	@Override
	public Object visitObject(SerializedObject value) {
		try {
			Object result = fetch(value, () -> GenericObject.newInstance(value.getObjectType()));
			for (SerializedField field : value.getFields()) {
				GenericObject.setField(result, field.getName(), field.getValue().accept(this));
			}
			return result;
		} catch (GenericObjectException e) {
			throw new DeserializationException(value.toString());
		}
	}

	@Override
	public Object visitBigDecimal(SerializedBigDecimal value) {
		return fetch(value, () -> value.getValue());
	}

	@Override
	public Object visitBigInteger(SerializedBigInteger value) {
		return fetch(value, () -> value.getValue());
	}

	@Override
	public Object visitList(SerializedList value) {
		List<Object> list = fetch(value, ArrayList::new);
		for (SerializedValue element : value) {
			list.add(element.accept(this));
		}
		return list;
	}

	@Override
	public Object visitMap(SerializedMap value) {
		Map<Object,Object> map = fetch(value, LinkedHashMap::new);
		for (Map.Entry<SerializedValue,SerializedValue> entry : value.entrySet()) {
			Object k = entry.getKey().accept(this);
			Object v = entry.getValue().accept(this);
			map.put(k, v);
		}
		return map;
	}

	@Override
	public Object visitSet(SerializedSet value) {
		Set<Object> set = fetch(value, LinkedHashSet::new);
		for (SerializedValue element : value) {
			set.add(element.accept(this));
		}
		return set;
	}

	@Override
	public Object visitArray(SerializedArray value) {
		Class<?> componentType = value.getRawType();
		SerializedValue[] rawArray = value.getArray();
		Object[] array = (Object[]) fetch(value, () -> Array.newInstance(componentType, rawArray.length));
		for (int i = 0; i < rawArray.length; i++) {
			array[i] = rawArray[i].accept(this);
		}
		return array;
	}

	@Override
	public Object visitLiteral(SerializedLiteral value) {
		return fetch(value, () -> ((SerializedLiteral) value).getValue());
	}

	@Override
	public Object visitNull(SerializedNull value) {
		return null;
	}

	@Override
	public Object visitUnknown(SerializedValue value) {
		throw new DeserializationException(value.toString());
	}

}
