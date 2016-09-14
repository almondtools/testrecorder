package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.deserializers.Templates.equalToMatcher;
import static net.amygdalum.testrecorder.util.Types.equalTypes;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.lang.reflect.Type;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultClassAdaptor extends DefaultMatcherGenerator<SerializedImmutable<Class<?>>> implements MatcherGenerator<SerializedImmutable<Class<?>>> {

	@SuppressWarnings("rawtypes")
	@Override
	public Class<SerializedImmutable> getAdaptedClass() {
		return SerializedImmutable.class;
	}

	@Override
	public boolean matches(Type type) {
		return equalTypes(type, Class.class);
	}

	@Override
	public Computation tryDeserialize(SerializedImmutable<Class<?>> value, MatcherGenerators generator) {
		TypeManager types = generator.getTypes();
		types.registerImport(Class.class);
		types.staticImport(Matchers.class, "equalTo");

		Class<?> clazz = value.getValue();

		String equalToMatcher = equalToMatcher(types.getRawTypeName(clazz));
		return new Computation(equalToMatcher, parameterized(Matcher.class, null, value.getType()), emptyList());
	}

}
