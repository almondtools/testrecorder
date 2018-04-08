package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class DefaultLiteralAdaptor extends DefaultSetupGenerator<SerializedLiteral> implements SetupGenerator<SerializedLiteral> {

	@Override
	public Class<SerializedLiteral> getAdaptedClass() {
		return SerializedLiteral.class;
	}

	@Override
	public Computation tryDeserialize(SerializedLiteral value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		
		Object literalValue = value.getValue();
		String literal = asLiteral(literalValue);
		return expression(literal, types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class));
	}

}
