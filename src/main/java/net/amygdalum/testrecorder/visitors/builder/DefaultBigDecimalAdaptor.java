package net.amygdalum.testrecorder.visitors.builder;

import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.newObject;

import java.math.BigDecimal;

import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultBigDecimalAdaptor extends DefaultAdaptor<SerializedBigDecimal, ObjectToSetupCode> implements Adaptor<SerializedBigDecimal, ObjectToSetupCode> {

	@Override
	public Computation tryDeserialize(SerializedBigDecimal value, TypeManager types, ObjectToSetupCode generator) {
		types.registerImport(BigDecimal.class);

		String literal = asLiteral(value.getValue().toPlainString());
		String bigDecimal = newObject("BigDecimal", literal);
		return new Computation(bigDecimal);
	}

}
