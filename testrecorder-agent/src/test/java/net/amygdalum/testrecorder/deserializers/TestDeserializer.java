package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.types.Computation.expression;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.ValuePrinter;

public class TestDeserializer extends MappedRoleVisitor<Computation, String> implements Deserializer {

	public TestDeserializer() {
		super(new ValuePrinter(), s -> expression("(" + s + ")", null));
	}

	@Override
	public DeserializerContext getContext() {
		return null;
	}

	public static class Factory implements DeserializerFactory {

		@Override
		public Deserializer newGenerator(DeserializerContext context) {
			return new TestDeserializer();
		}

	}

}
