package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static net.amygdalum.testrecorder.deserializers.Templates.enumMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.fieldAccess;
import static net.amygdalum.testrecorder.deserializers.Templates.sameInstanceMatcher;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcardExtends;

import java.lang.reflect.Type;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.EnumMatcher;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class DefaultEnumAdaptor extends DefaultMatcherGenerator<SerializedEnum> implements MatcherGenerator<SerializedEnum> {
	
	@Override
	public Class<SerializedEnum> getAdaptedClass() {
		return SerializedEnum.class;
	}

	@Override
	public boolean matches(Type type) {
		return baseType(type).isEnum();
	}

	@Override
	public Computation tryDeserialize(SerializedEnum value, MatcherGenerators generator) {
		TypeManager types = generator.getTypes();
		types.registerType(value.getType());

		if (types.isHidden(value.getType())) {
			types.staticImport(EnumMatcher.class, "matchingEnum");
		} else {
			types.staticImport(Matchers.class, "sameInstance");
		}

		if (types.isHidden(value.getType())) {
			String enumMatcher = enumMatcher(asLiteral(value.getName()));
			return new Computation(enumMatcher, parameterized(Matcher.class, null, wildcardExtends(Enum.class)), emptyList());
		} else {
			String typeName = types.getBestName(value.getType());
			String name = value.getName();

			String matchingValue = fieldAccess(typeName, name);
			String enumMatcher = sameInstanceMatcher(matchingValue);
			return new Computation(enumMatcher, parameterized(Matcher.class, null, value.getType()), emptyList());
		}
	}

}
