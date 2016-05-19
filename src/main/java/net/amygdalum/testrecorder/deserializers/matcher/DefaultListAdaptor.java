package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.containsMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.emptyMatcher;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListAdaptor extends DefaultAdaptor<SerializedList, ObjectToMatcherCode> implements Adaptor<SerializedList, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedList value, ObjectToMatcherCode generator) {
		TypeManager types = generator.getTypes();
		if (value.isEmpty()) {
			types.staticImport(Matchers.class, "empty");

			return new Computation(emptyMatcher(), parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			types.staticImport(Matchers.class, "contains");

			List<Computation> elements = value.stream()
				.map(element -> generator.simpleMatcher(element))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String containsMatcher = containsMatcher(elementValues);

			return new Computation(containsMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}
	}

}
