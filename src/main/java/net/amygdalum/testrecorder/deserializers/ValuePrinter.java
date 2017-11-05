package net.amygdalum.testrecorder.deserializers;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValueType;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedEnum;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class ValuePrinter implements Deserializer<String> {

	private Set<Object> known;

	public ValuePrinter() {
		known = new LinkedHashSet<>();
	}

	@Override
	public String visitField(SerializedField field, DeserializerContext context) {
		return field.getType().getTypeName() + " " + field.getName() + ": " + field.getValue().accept(this, context);
	}

	@Override
	public String visitReferenceType(SerializedReferenceType rt, DeserializerContext context) {
		if (rt instanceof SerializedObject) {
			SerializedObject value = (SerializedObject) rt;
			boolean inserted = known.add(value);
			if (inserted) {
				return value.getType().getTypeName() + "/" + System.identityHashCode(value) + " "
					+ value.getFields().stream()
						.sorted()
						.map(field -> field.accept(this, context))
						.collect(joining(",\n", "{\n", "\n}"));
			} else {
				return value.getType() + "/" + System.identityHashCode(value);
			}
		} else if (rt instanceof SerializedList) {
			SerializedList value = (SerializedList) rt;
			return value.stream()
				.map(element -> element.accept(this, context))
				.collect(joining(", ", "[", "]"));
		} else if (rt instanceof SerializedMap) {
			SerializedMap value = (SerializedMap) rt;
			return value.entrySet().stream()
				.map(element -> element.getKey().accept(this, context) + ":" + element.getValue().accept(this, context))
				.collect(joining(",", "{", "}"));
		} else if (rt instanceof SerializedSet) {
			SerializedSet value = (SerializedSet) rt;
			return value.stream()
				.map(element -> element.accept(this, context))
				.collect(joining(", ", "{", "}"));
		} else if (rt instanceof SerializedArray) {
			SerializedArray value = (SerializedArray) rt;
			return Stream.of(value.getArray())
				.map(element -> element.accept(this, context))
				.collect(joining(", ", "<", ">"));
		} else if (rt instanceof SerializedNull) {
			return "null";
		} else {
			return "";
		}
	}

	@Override
	public String visitImmutableType(SerializedImmutableType rt, DeserializerContext context) {
		if (rt instanceof SerializedImmutable<?>) {
			SerializedImmutable<?> value = (SerializedImmutable<?>) rt;
			return value.getValue().toString();
		} else if (rt instanceof SerializedEnum) {
			SerializedEnum value = (SerializedEnum) rt;
			return value.getName();
		} else{
			return "";
		}
	}

	@Override
	public String visitValueType(SerializedValueType value, DeserializerContext context) {
		return value.getValue().toString();
	}

}
