package net.amygdalum.testrecorder.values;

import static java.util.stream.Collectors.joining;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedArgument;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedResult;
import net.amygdalum.testrecorder.types.SerializedRole;
import net.amygdalum.testrecorder.types.SerializedValueType;

public class ValuePrinter implements RoleVisitor<String> {

	private Set<Object> known;

	public ValuePrinter() {
		known = new LinkedHashSet<>();
	}

	public static String print(SerializedRole value) {
		ValuePrinter printer = new ValuePrinter();
		return value.accept(printer);
	}

	@Override
	public String visitField(SerializedField field) {
		return field.getType().getTypeName() + " " + field.getName() + ": " + field.getValue().accept(this);
	}

	@Override
	public String visitArgument(SerializedArgument argument) {
		return argument.getValue().accept(this);
	}

	@Override
	public String visitResult(SerializedResult result) {
		return result.getValue().accept(this);
	}

	@Override
	public String visitReferenceType(SerializedReferenceType rt) {
		boolean inserted = known.add(rt);
		if (!inserted) {
			return rt.getType() + "/" + System.identityHashCode(rt);
		} else if (rt instanceof SerializedObject) {
			SerializedObject value = (SerializedObject) rt;
			return printObject(value);
		} else if (rt instanceof SerializedProxy) {
			SerializedProxy value = (SerializedProxy) rt;
			return printProxy(value);
		} else if (rt instanceof SerializedList) {
			SerializedList value = (SerializedList) rt;
			return printList(value);
		} else if (rt instanceof SerializedMap) {
			SerializedMap value = (SerializedMap) rt;
			return printMap(value);
		} else if (rt instanceof SerializedSet) {
			SerializedSet value = (SerializedSet) rt;
			return printSet(value);
		} else if (rt instanceof SerializedArray) {
			SerializedArray value = (SerializedArray) rt;
			return printArray(value);
		} else if (rt == SerializedNull.VOID) {
			return "void";
		} else if (rt instanceof SerializedNull) {
			return "null";
		} else {
			return "?";
		}
	}

	private String printProxy(SerializedProxy value) {
		return value.getType().toString().replace("class", "proxy") + "/" + System.identityHashCode(value);
	}

	private String printObject(SerializedObject value) {
		return value.getType().getTypeName() + "/" + System.identityHashCode(value) + " "
			+ value.getFields().stream()
				.sorted()
				.map(field -> field.accept(this))
				.collect(joining(",\n", "{\n", "\n}"));
	}

	private String printList(SerializedList value) {
		return value.stream()
			.map(element -> element.accept(this))
			.collect(joining(", ", "[", "]"));
	}

	private String printSet(SerializedSet value) {
		return value.stream()
			.map(element -> element.accept(this))
			.collect(joining(", ", "{", "}"));
	}

	private String printMap(SerializedMap value) {
		return value.entrySet().stream()
			.map(element -> element.getKey().accept(this) + ":" + element.getValue().accept(this))
			.collect(joining(",", "{", "}"));
	}

	private String printArray(SerializedArray value) {
		return Stream.of(value.getArray())
			.map(element -> element.accept(this))
			.collect(joining(", ", "<", ">"));
	}

	@Override
	public String visitImmutableType(SerializedImmutableType rt) {
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
	public String visitValueType(SerializedValueType value) {
		return value.getValue().toString();
	}

}
