package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedFieldType;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializedValueType;

public class ValuePrinter implements Deserializer<String> {

	private Set<Object> known;

	public ValuePrinter() {
		known = new LinkedHashSet<>();
	}

	public static String print(SerializedValue value) {
		ValuePrinter printer = new ValuePrinter();
		return printer.printValue(value);
	}

	public static String print(SerializedFieldType value) {
		ValuePrinter printer = new ValuePrinter();
		return printer.printField(value);
	}

	public String printValue(SerializedValue value) {
		return value.accept(this, DeserializerContext.NULL);
	}

	public String printField(SerializedFieldType value) {
		return value.accept(this, DeserializerContext.NULL);
	}

	@Override
	public String visitField(SerializedFieldType field, DeserializerContext context) {
		return field.getType().getTypeName() + " " + field.getName() + ": " + field.getValue().accept(this, context);
	}

	@Override
	public String visitReferenceType(SerializedReferenceType rt, DeserializerContext context) {
		boolean inserted = known.add(rt);
		if (!inserted) {
			return rt.getType() + "/" + System.identityHashCode(rt);
		} else if (rt instanceof SerializedObject) {
			SerializedObject value = (SerializedObject) rt;
			return printObject(context, value); 
		} else if (rt instanceof SerializedProxy) {
			SerializedProxy value = (SerializedProxy) rt;
			return printProxy(context, value);
		} else if (rt instanceof SerializedPlaceholder) {
			SerializedPlaceholder value = (SerializedPlaceholder) rt;
			return printPlaceholder(context, value);
		} else if (rt instanceof SerializedList) {
			SerializedList value = (SerializedList) rt;
			return printList(context, value);
		} else if (rt instanceof SerializedMap) {
			SerializedMap value = (SerializedMap) rt;
			return printMap(context, value);
		} else if (rt instanceof SerializedSet) {
			SerializedSet value = (SerializedSet) rt;
			return printSet(context, value);
		} else if (rt instanceof SerializedArray) {
			SerializedArray value = (SerializedArray) rt;
			return printArray(context, value);
		} else if (rt instanceof SerializedNull) {
			return "null";
		} else {
			return "?";
		}
	}

	private String printPlaceholder(DeserializerContext context, SerializedPlaceholder value) {
		return value.getType().toString().replace("class", "placeholder") + "/" + System.identityHashCode(value);
	}

	private String printProxy(DeserializerContext context, SerializedProxy value) {
		return value.getType().toString().replace("class", "proxy") + "/" + System.identityHashCode(value);
	}

	private String printObject(DeserializerContext context, SerializedObject value) {
		return value.getType().getTypeName() + "/" + System.identityHashCode(value) + " "
			+ value.getFields().stream()
				.sorted()
				.map(field -> field.accept(this, context))
				.collect(joining(",\n", "{\n", "\n}"));
	}

	private String printList(DeserializerContext context, SerializedList value) {
		return value.stream()
			.map(element -> element.accept(this, context))
			.collect(joining(", ", "[", "]"));
	}

	private String printSet(DeserializerContext context, SerializedSet value) {
		return value.stream()
			.map(element -> element.accept(this, context))
			.collect(joining(", ", "{", "}"));
	}

	private String printMap(DeserializerContext context, SerializedMap value) {
		return value.entrySet().stream()
			.map(element -> element.getKey().accept(this, context) + ":" + element.getValue().accept(this, context))
			.collect(joining(",", "{", "}"));
	}

	private String printArray(DeserializerContext context, SerializedArray value) {
		return Stream.of(value.getArray())
			.map(element -> element.accept(this, context))
			.collect(joining(", ", "<", ">"));
	}

	@Override
	public String visitImmutableType(SerializedImmutableType rt, DeserializerContext context) {
		if (rt instanceof SerializedImmutable<?>) {
			SerializedImmutable<?> value = (SerializedImmutable<?>) rt;
			return value.getValue().toString();
		} else if (rt instanceof SerializedEnum) {
			SerializedEnum value = (SerializedEnum) rt;
			return value.getName();
		} else {
			return "";
		}
	}

	@Override
	public String visitValueType(SerializedValueType value, DeserializerContext context) {
		return value.getValue().toString();
	}

}
