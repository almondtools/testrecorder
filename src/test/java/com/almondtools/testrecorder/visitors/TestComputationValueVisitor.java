package com.almondtools.testrecorder.visitors;

public class TestComputationValueVisitor extends MappedSerializedValueVisitor<Computation, String> {

	public TestComputationValueVisitor() {
		super(new SerializedValuePrinter(), s -> new Computation("(" + s + ")"));
	}
}
