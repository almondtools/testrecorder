package net.amygdalum.testrecorder.visitors.builder;

import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.newObject;

import java.math.BigInteger;

import net.amygdalum.testrecorder.values.SerializedBigInteger;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;


public class DefaultBigIntegerAdaptor extends DefaultAdaptor<SerializedBigInteger, ObjectToSetupCode> implements Adaptor<SerializedBigInteger, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedBigInteger value, TypeManager types, ObjectToSetupCode generator) {
		types.registerImport(BigInteger.class);

		String literal = asLiteral(value.getValue().toString());
		String bigInteger = newObject("BigInteger", literal);
		return new Computation(bigInteger);
	}

}
