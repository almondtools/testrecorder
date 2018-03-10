package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.types.Computation.expression;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.values.ValuePrinter;

public class TestComputationValueVisitor extends MappedDeserializer<Computation, String> {

	public TestComputationValueVisitor() {
		super(new ValuePrinter(), s -> expression("(" + s + ")", null));
	}
}
