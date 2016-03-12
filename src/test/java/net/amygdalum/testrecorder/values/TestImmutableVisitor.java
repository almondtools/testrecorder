package net.amygdalum.testrecorder.values;

import net.amygdalum.testrecorder.visitors.TestValueVisitor;

import net.amygdalum.testrecorder.SerializedImmutableVisitor;
import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.values.SerializedBigInteger;

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