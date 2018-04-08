package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.containsInAnyOrderMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.emptyMatcher;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Types.isGeneric;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.runtime.ContainsMatcher;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptor extends DefaultMatcherGenerator<SerializedSet> implements MatcherGenerator<SerializedSet> {

	@Override
	public Class<SerializedSet> getAdaptedClass() {
		return SerializedSet.class;
	}

	@Override
	public Computation tryDeserialize(SerializedSet value, MatcherGenerators generator, DeserializerContext context) {
		Type componentType = value.getComponentType();

		TypeManager types = context.getTypes();
		if (types.isHidden(componentType)) {
			componentType = Object.class;
		}

		String elementType = types.getRawClass(componentType);
		if (isGeneric(componentType)) {
			elementType = context.adapt(elementType, parameterized(Class.class, null, componentType), parameterized(Class.class, null, wildcard()));
		}

		if (value.isEmpty()) {
			types.staticImport(Matchers.class, "empty");

			return expression(emptyMatcher(), parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			types.staticImport(ContainsMatcher.class, "contains");

			List<Computation> elements = value.stream()
				.map(element -> generator.simpleMatcher(element, context))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String containsInAnyOrderMatcher = containsInAnyOrderMatcher(elementType, elementValues);

			return expression(containsInAnyOrderMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}
	}

}
