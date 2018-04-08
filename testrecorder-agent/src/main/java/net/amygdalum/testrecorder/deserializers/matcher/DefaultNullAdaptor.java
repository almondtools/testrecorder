package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.nullMatcher;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.Optional;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptor extends DefaultMatcherGenerator<SerializedNull> implements MatcherGenerator<SerializedNull> {

	@Override
	public Class<SerializedNull> getAdaptedClass() {
		return SerializedNull.class;
	}

	@Override
	public Computation tryDeserialize(SerializedNull value, MatcherGenerators generator, DeserializerContext context) {
		TypeManager types = context.getTypes();
		types.registerType(value.getType());
		types.registerTypes(value.getUsedTypes());
		types.staticImport(Matchers.class, "nullValue");

		Optional<Type> usedType = types.mostSpecialOf(value.getUsedTypes());
		if (!types.isHidden(value.getType())) {
			String nullMatcher = nullMatcher(types.getRawClass(value.getType()));
			return expression(nullMatcher, parameterized(Matcher.class, null, value.getType()), emptyList());
		} else if (usedType.isPresent()) {
			Type visibleUsedType = usedType.get();
			String nullMatcher = nullMatcher(types.getRawClass(visibleUsedType));
			return expression(nullMatcher, parameterized(Matcher.class, null, visibleUsedType), emptyList());
		} else {
			String nullMatcher = nullMatcher("");
			return expression(nullMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		}
	}

}
