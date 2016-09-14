package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.nullMatcher;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptor extends DefaultMatcherGenerator<SerializedNull> implements MatcherGenerator<SerializedNull> {

	@Override
	public Class<SerializedNull> getAdaptedClass() {
		return SerializedNull.class;
	}

	@Override
	public Computation tryDeserialize(SerializedNull value, MatcherGenerators generator) {
		TypeManager types = generator.getTypes();
		types.registerType(value.getType());
		types.staticImport(Matchers.class, "nullValue");

		if (!types.isHidden(value.getType())) {
			String nullMatcher = nullMatcher(types.getRawTypeName(value.getType()));
			return new Computation(nullMatcher, parameterized(Matcher.class, null, value.getType()), emptyList());
		} else if (!types.isHidden(value.getResultType())) {
			String nullMatcher = nullMatcher(types.getRawTypeName(value.getResultType()));
			return new Computation(nullMatcher, parameterized(Matcher.class, null, value.getResultType()), emptyList());
		} else {
			String nullMatcher = nullMatcher("");
			return new Computation(nullMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		}
	}

}
