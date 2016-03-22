package net.amygdalum.testrecorder.visitors.builder;

import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;

import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultLiteralAdaptor extends DefaultAdaptor<SerializedLiteral, ObjectToSetupCode> implements Adaptor<SerializedLiteral, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedLiteral value, TypeManager types, ObjectToSetupCode generator) {
		Object literalValue = value.getValue();
		String literal = asLiteral(literalValue);
		return new Computation(literal);
	}

}
