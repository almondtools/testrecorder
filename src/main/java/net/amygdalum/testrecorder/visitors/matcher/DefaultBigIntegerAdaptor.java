package net.amygdalum.testrecorder.visitors.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.equalToMatcher;
import static net.amygdalum.testrecorder.visitors.Templates.newObject;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;

import java.math.BigInteger;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.values.SerializedBigInteger;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;


public class DefaultBigIntegerAdaptor extends DefaultAdaptor<SerializedBigInteger, ObjectToMatcherCode> implements Adaptor<SerializedBigInteger, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedBigInteger value, TypeManager types, ObjectToMatcherCode generator) {
		types.registerImport(BigInteger.class);
		types.staticImport(Matchers.class, "equalTo");

		String literal = asLiteral(value.getValue().toString());

		String bigIntegerLiteral = newObject("BigInteger", literal);

		String equalToMatcher = equalToMatcher(bigIntegerLiteral);
		return new Computation(equalToMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

}
