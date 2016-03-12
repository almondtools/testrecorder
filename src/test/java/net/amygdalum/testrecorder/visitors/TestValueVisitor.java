package net.amygdalum.testrecorder.visitors;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class TestValueVisitor implements SerializedValueVisitor<String> {

	@Override
	public String visitField(SerializedField field) {
		return "field";
	}

	@Override
	public String visitObject(SerializedObject value) {
		return "object";
	}

	@Override
	public String visitArray(SerializedArray value) {
		return "array";
	}

	@Override
	public String visitLiteral(SerializedLiteral value) {
		return "literal";
	}

	@Override
	public String visitNull(SerializedNull value) {
		return "null";
	}

	@Override
	public String visitUnknown(SerializedValue value) {
		return "unknown";
	}
}