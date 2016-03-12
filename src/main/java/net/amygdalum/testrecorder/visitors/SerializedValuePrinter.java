package net.amygdalum.testrecorder.visitors;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedCollectionVisitor;
import net.amygdalum.testrecorder.SerializedImmutableVisitor;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.values.SerializedBigInteger;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

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
