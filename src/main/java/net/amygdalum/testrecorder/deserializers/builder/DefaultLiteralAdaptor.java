package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class DefaultLiteralAdaptor extends DefaultAdaptor<SerializedLiteral, ObjectToSetupCode> implements Adaptor<SerializedLiteral, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedLiteral value, ObjectToSetupCode generator) {
		Object literalValue = value.getValue();
		String literal = asLiteral(literalValue);
		return new Computation(literal, value.getResultType());
	}

}
