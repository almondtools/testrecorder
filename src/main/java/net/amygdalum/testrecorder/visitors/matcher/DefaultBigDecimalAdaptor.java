package net.amygdalum.testrecorder.visitors.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.equalToMatcher;
import static net.amygdalum.testrecorder.visitors.Templates.newObject;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;

import java.math.BigDecimal;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;


public class DefaultBigDecimalAdaptor extends DefaultAdaptor<SerializedBigDecimal, ObjectToMatcherCode> implements Adaptor<SerializedBigDecimal, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedBigDecimal value, TypeManager types, ObjectToMatcherCode generator) {
		types.registerImport(BigDecimal.class);
		types.staticImport(Matchers.class, "equalTo");

		String literal = asLiteral(value.getValue().toPlainString());

		String bigDecimalLiteral = newObject("BigDecimal", literal);

		String equalToMatcher = equalToMatcher(bigDecimalLiteral);
		return new Computation(equalToMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

}
