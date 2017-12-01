package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Computation.expression;
import static net.amygdalum.testrecorder.deserializers.Templates.containsInOrderMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.emptyMatcher;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.runtime.ContainsInOrderMatcher;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultSequenceAdaptor extends DefaultMatcherGenerator<SerializedList> implements MatcherGenerator<SerializedList> {

	@Override
	public Class<SerializedList> getAdaptedClass() {
		return SerializedList.class;
	}

	@Override
	public Computation tryDeserialize(SerializedList value, MatcherGenerators generator, DeserializerContext context) {
        Type componentType = value.getComponentType();

        TypeManager types = generator.getTypes();
        if (types.isHidden(componentType)) {
            componentType = Object.class;
        }
        
		if (value.isEmpty()) {
			types.staticImport(Matchers.class, "empty");

			return expression(emptyMatcher(), parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			types.staticImport(ContainsInOrderMatcher.class, "containsInOrder");

			List<Computation> elements = value.stream()
				.map(element -> generator.simpleMatcher(element, context))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

	        String elementType = types.getRawTypeName(componentType);
			String containsMatcher = containsInOrderMatcher(elementType, elementValues);

			return expression(containsMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}
	}

}
