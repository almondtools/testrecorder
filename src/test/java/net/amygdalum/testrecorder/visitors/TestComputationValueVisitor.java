package net.amygdalum.testrecorder.visitors;

import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.MappedSerializedValueVisitor;
import net.amygdalum.testrecorder.visitors.SerializedValuePrinter;

public class TestComputationValueVisitor extends MappedSerializedValueVisitor<Computation, String> {

	public TestComputationValueVisitor() {
		super(new SerializedValuePrinter(), s -> new Computation("(" + s + ")"));
	}
}
