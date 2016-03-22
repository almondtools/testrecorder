package net.amygdalum.testrecorder.visitors.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.visitors.Templates.containsInAnyOrderMatcher;
import static net.amygdalum.testrecorder.visitors.Templates.emptyMatcher;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;
import static net.amygdalum.testrecorder.visitors.TypeManager.wildcard;

import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import net.amygdalum.testrecorder.values.SerializedSet;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultSetAdaptor extends DefaultAdaptor<SerializedSet, ObjectToMatcherCode> implements Adaptor<SerializedSet, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedSet value, TypeManager types, ObjectToMatcherCode generator) {
		if (value.isEmpty()) {
			types.staticImport(Matchers.class, "empty");

			String emptyMatcher = emptyMatcher();
			return new Computation(emptyMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			types.staticImport(Matchers.class, "containsInAnyOrder");

			List<Computation> elements = value.stream()
				.map(element -> generator.simpleValue(element))
				.collect(toList());

			List<String> elementComputations = elements.stream()
				.flatMap(element -> element.getStatements().stream())
				.collect(toList());

			String[] elementValues = elements.stream()
				.map(element -> element.getValue())
				.toArray(String[]::new);

			String containsInAnyOrderMatcher = containsInAnyOrderMatcher(elementValues);
			return new Computation(containsInAnyOrderMatcher, parameterized(Matcher.class, null, wildcard()), elementComputations);
		}
	}

}
