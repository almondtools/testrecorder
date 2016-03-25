package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;

import java.math.BigDecimal;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigDecimalAdaptor extends DefaultAdaptor<SerializedImmutable<BigDecimal>, ObjectToSetupCode> implements Adaptor<SerializedImmutable<BigDecimal>, ObjectToSetupCode> {

	@Override
	public boolean matches(Class<?> clazz) {
		return clazz.equals(BigDecimal.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<BigDecimal> value, TypeManager types, ObjectToSetupCode generator) {
		types.registerImport(BigDecimal.class);

		String literal = asLiteral(value.getValue().toPlainString());
		String bigDecimal = newObject("BigDecimal", literal);
		return new Computation(bigDecimal);
	}

}
