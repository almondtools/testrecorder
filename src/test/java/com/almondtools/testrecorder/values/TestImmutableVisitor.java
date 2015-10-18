package com.almondtools.testrecorder.values;

import com.almondtools.testrecorder.SerializedImmutableVisitor;

public class TestImmutableVisitor extends TestValueVisitor implements SerializedImmutableVisitor<String> {

	@Override
	public String visitBigDecimal(SerializedBigDecimal value) {
		return "bigDecimal";
	}

	@Override
	public String visitBigInteger(SerializedBigInteger value) {
		return "bigInteger";
	}

}