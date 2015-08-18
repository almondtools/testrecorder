package com.almondtools.invitroderivatives.visitors;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.almondtools.invitroderivatives.SerializedValueVisitor;
import com.almondtools.invitroderivatives.values.SerializedArray;
import com.almondtools.invitroderivatives.values.SerializedField;
import com.almondtools.invitroderivatives.values.SerializedList;
import com.almondtools.invitroderivatives.values.SerializedLiteral;
import com.almondtools.invitroderivatives.values.SerializedMap;
import com.almondtools.invitroderivatives.values.SerializedNull;
import com.almondtools.invitroderivatives.values.SerializedObject;
import com.almondtools.invitroderivatives.values.SerializedSet;

public class SerializedValuePrinter implements SerializedValueVisitor<String> {

	private Set<Object> known;

	public SerializedValuePrinter() {
		known = new LinkedHashSet<>();
	}

	@Override
	public String visitObject(SerializedObject object) {
		boolean inserted = known.add(object);
		if (inserted) {
			return object.getType().getSimpleName() + "/" + System.identityHashCode(object) + " "
				+ object.getFields().stream()
					.map(field -> field.accept(this))
					.collect(joining(",\n", "{\n", "\n}"));
		} else {
			return object.getType() + "/" + System.identityHashCode(object);
		}
	}

	@Override
	public String visitField(SerializedField field) {
		return field.getType().getSimpleName() + " " + field.getName() + ": " + field.getValue().accept(this);
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
}
