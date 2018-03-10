package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.equalToMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.newObject;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.lang.reflect.Type;
import java.math.BigInteger;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigIntegerAdaptor extends DefaultMatcherGenerator<SerializedImmutable<BigInteger>> implements MatcherGenerator<SerializedImmutable<BigInteger>> {

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
	public Computation tryDeserialize(SerializedImmutable<BigInteger> value, MatcherGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerImport(BigInteger.class);
		types.staticImport(Matchers.class, "equalTo");

		String literal = asLiteral(value.getValue().toString());

		String bigIntegerLiteral = newObject("BigInteger", literal);

		String equalToMatcher = equalToMatcher(bigIntegerLiteral);
		return expression(equalToMatcher, parameterized(Matcher.class, null, value.getType()), emptyList());
	}

}
