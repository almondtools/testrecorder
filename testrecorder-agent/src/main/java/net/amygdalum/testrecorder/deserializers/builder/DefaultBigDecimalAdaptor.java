package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Types.equalBaseTypes;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigDecimalAdaptor extends DefaultSetupGenerator<SerializedImmutable<BigDecimal>> implements Adaptor<SerializedImmutable<BigDecimal>, SetupGenerators> {

	@SuppressWarnings("rawtypes")
	@Override
	public Class<SerializedImmutable> getAdaptedClass() {
		return SerializedImmutable.class;
	}

	@Override
	public boolean matches(Type type) {
		return equalBaseTypes(type, BigDecimal.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<BigDecimal> value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(BigDecimal.class);

		String literal = asLiteral(value.getValue().toPlainString());
		String bigDecimal = newObject("BigDecimal", literal);
		return expression(bigDecimal, types.mostSpecialOf(value.getUsedTypes()).orElse(BigDecimal.class));
	}

}
