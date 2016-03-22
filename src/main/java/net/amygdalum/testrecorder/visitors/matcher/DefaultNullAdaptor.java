package net.amygdalum.testrecorder.visitors.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.visitors.Templates.nullMatcher;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultNullAdaptor extends DefaultAdaptor<SerializedNull, ObjectToMatcherCode> implements Adaptor<SerializedNull, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedNull value, TypeManager types, ObjectToMatcherCode generator) {
		types.registerImport(value.getValueType());
		types.staticImport(Matchers.class, "nullValue");

		String nullMatcher = nullMatcher(types.getRawName(value.getValueType()));
		return new Computation(nullMatcher, parameterized(Matcher.class, null, value.getValueType()), emptyList());
	}

}
