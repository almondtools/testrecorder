package net.amygdalum.testrecorder.visitors.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.visitors.Templates.asLiteral;
import static net.amygdalum.testrecorder.visitors.Templates.equalToMatcher;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultLiteralAdaptor extends DefaultAdaptor<SerializedLiteral, ObjectToMatcherCode> implements Adaptor<SerializedLiteral, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedLiteral value, TypeManager types, ObjectToMatcherCode generator) {
		types.staticImport(Matchers.class, "equalTo");

		String valueExpression = asLiteral(value.getValue());

		String equalToMatcher = equalToMatcher(valueExpression);
		return new Computation(equalToMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

}
