package com.almondtools.testrecorder.visitors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Array;

import com.almondtools.testrecorder.DeserializationException;
import com.almondtools.testrecorder.GenericObject;
import com.almondtools.testrecorder.GenericObjectException;
import com.almondtools.testrecorder.SerializedCollectionVisitor;
import com.almondtools.testrecorder.SerializedImmutableVisitor;
import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
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

	@Override
	public Object visitField(SerializedField field) {
		throw new DeserializationException(field.toString());
	}

	@Override
	public Object visitObject(SerializedObject value) {
		try {
			Object result = GenericObject.newInstance(value.getObjectType());
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
		return value.getValue();
	}

	@Override
	public Object visitBigInteger(SerializedBigInteger value) {
		return value.getValue();
	}

	@Override
	public Object visitList(SerializedList value) {
		return value.stream()
			.map(element -> element.accept(this))
			.collect(toList());
	}

	@Override
	public Object visitMap(SerializedMap value) {
		return value.entrySet().stream()
			.collect(toMap(entry -> entry.getKey().accept(this), entry -> entry.getValue().accept(this)));
	}

	@Override
	public Object visitSet(SerializedSet value) {
		return value.stream()
			.map(element -> element.accept(this))
			.collect(toSet());
	}

	@Override
	public Object visitArray(SerializedArray value) {
		Class<?> componentType = value.getRawType();
		return value.getArrayAsList().stream()
			.map(element -> element.accept(this))
			.toArray(len -> (Object[]) Array.newInstance(componentType, len));
	}

	@Override
	public Object visitLiteral(SerializedLiteral value) {
		return ((SerializedLiteral) value).getValue();
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
