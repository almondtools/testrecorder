package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.nullMatcher;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptor extends DefaultAdaptor<SerializedNull, ObjectToMatcherCode> implements Adaptor<SerializedNull, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedNull value, ObjectToMatcherCode generator) {
		TypeManager types = generator.getTypes();
		types.registerType(value.getType());
		types.staticImport(Matchers.class, "nullValue");

		String nullMatcher = nullMatcher(types.getRawTypeName(value.getType()));
		return new Computation(nullMatcher, parameterized(Matcher.class, null, value.getType()), emptyList());
	}

}
