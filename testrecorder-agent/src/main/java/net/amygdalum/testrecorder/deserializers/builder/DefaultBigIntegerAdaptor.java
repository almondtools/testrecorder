package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Types.equalTypes;

import java.lang.reflect.Type;
import java.math.BigInteger;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigIntegerAdaptor extends DefaultSetupGenerator<SerializedImmutable<BigInteger>> implements Adaptor<SerializedImmutable<BigInteger>, SetupGenerators> {

	@SuppressWarnings("rawtypes")
	@Override
	public Class<SerializedImmutable> getAdaptedClass() {
		return SerializedImmutable.class;
	}

	@Override
	public boolean matches(Type type) {
		return equalTypes(type, BigInteger.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<BigInteger> value, SetupGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(BigInteger.class);

		String literal = asLiteral(value.getValue().toString());
		String bigInteger = newObject("BigInteger", literal);
		return expression(bigInteger, types.mostSpecialOf(value.getUsedTypes()).orElse(BigInteger.class));
	}

}
