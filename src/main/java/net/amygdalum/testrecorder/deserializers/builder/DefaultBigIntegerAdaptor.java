package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;
import java.math.BigInteger;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigIntegerAdaptor extends DefaultAdaptor<SerializedImmutable<BigInteger>, ObjectToSetupCode> implements Adaptor<SerializedImmutable<BigInteger>, ObjectToSetupCode> {

	@Override
	public boolean matches(Type type) {
		return equalTypes(type, BigInteger.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<BigInteger> value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerImport(BigInteger.class);

		String literal = asLiteral(value.getValue().toString());
		String bigInteger = newObject("BigInteger", literal);
		return new Computation(bigInteger, value.getResultType());
	}

}
