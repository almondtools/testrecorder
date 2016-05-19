package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigDecimalAdaptor extends DefaultAdaptor<SerializedImmutable<BigDecimal>, ObjectToSetupCode> implements Adaptor<SerializedImmutable<BigDecimal>, ObjectToSetupCode> {

	@Override
	public boolean matches(Type type) {
		return equalTypes(type, BigDecimal.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<BigDecimal> value, ObjectToSetupCode generator) {
		TypeManager types = generator.getTypes();
		types.registerImport(BigDecimal.class);

		String literal = asLiteral(value.getValue().toPlainString());
		String bigDecimal = newObject("BigDecimal", literal);
		return new Computation(bigDecimal, value.getResultType());
	}

}
