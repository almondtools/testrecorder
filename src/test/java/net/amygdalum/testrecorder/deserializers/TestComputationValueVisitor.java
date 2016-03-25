package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.MappedDeserializer;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

public class TestComputationValueVisitor extends MappedDeserializer<Computation, String> {

	public TestComputationValueVisitor() {
		super(new ValuePrinter(), s -> new Computation("(" + s + ")"));
	}
}
