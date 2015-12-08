package com.almondtools.testrecorder.visitors;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

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

public class SerializedValuePrinter implements SerializedValueVisitor<String>, SerializedCollectionVisitor<String>, SerializedImmutableVisitor<String> {

	private Set<Object> known;

	public SerializedValuePrinter() {
		known = new LinkedHashSet<>();
	}

	@Override
	public String visitObject(SerializedObject object) {
		boolean inserted = known.add(object);
		if (inserted) {
			return object.getValueType().getTypeName() + "/" + System.identityHashCode(object) + " "
				+ object.getFields().stream()
					.sorted()
					.map(field -> field.accept(this))
					.collect(joining(",\n", "{\n", "\n}"));
		} else {
			return object.getValueType() + "/" + System.identityHashCode(object);
		}
	}

	@Override
	public String visitField(SerializedField field) {
		return field.getType().getTypeName() + " " + field.getName() + ": " + field.getValue().accept(this);
	}

	@Override
	public String visitList(SerializedList value) {
		return value.stream()
			.map(element -> element.accept(this))
			.collect(joining(", ", "[", "]"));
	}

	@Override
	public String visitSet(SerializedSet value) {
		return value.stream()
			.map(element -> element.accept(this))
			.collect(joining(", ", "{", "}"));
	}

	@Override
	public String visitMap(SerializedMap value) {
		return value.entrySet().stream()
			.map(element -> element.getKey().accept(this) + ":" + element.getValue().accept(this))
			.collect(joining(",", "{", "}"));
	}

	@Override
	public String visitArray(SerializedArray value) {
		return Stream.of(value.getArray())
			.map(element -> element.accept(this))
			.collect(joining(", ", "<", ">"));
	}

	@Override
	public String visitLiteral(SerializedLiteral value) {
		return value.getValue().toString();
	}

	@Override
	public String visitNull(SerializedNull value) {
		return "null";
	}

	@Override
	public String visitBigDecimal(SerializedBigDecimal value) {
		return value.getValue().toPlainString();
	}

	@Override
	public String visitBigInteger(SerializedBigInteger value) {
		return value.getValue().toString();
	}

	@Override
	public String visitUnknown(SerializedValue value) {
		return "";
	}

}
