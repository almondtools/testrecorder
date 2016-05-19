package net.amygdalum.testrecorder.deserializers;

public class TestComputationValueVisitor extends MappedDeserializer<Computation, String> {

	public TestComputationValueVisitor() {
		super(new ValuePrinter(), s -> new Computation("(" + s + ")", null));
	}
}
