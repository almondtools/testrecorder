package com.almondtools.testrecorder.values;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;

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