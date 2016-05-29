package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.containsEntriesMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.noEntriesMatcher;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.MapMatcher;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptor extends DefaultAdaptor<SerializedMap, ObjectToMatcherCode> implements Adaptor<SerializedMap, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedMap value, ObjectToMatcherCode generator) {
		TypeManager types = generator.getTypes();
		String keyType = types.getSimpleName(value.getMapKeyType());
		String valueType = types.getSimpleName(value.getMapValueType());
		if (value.isEmpty()) {
			types.staticImport(MapMatcher.class, "noEntries");

			String noEntriesMatcher = noEntriesMatcher(keyType, valueType);

			return new Computation(noEntriesMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			types.staticImport(MapMatcher.class, "containsEntries");

			List<Pair<Computation, Computation>> elements = value.entrySet().stream()
				.map(entry -> new Pair<>(
					generator.simpleMatcher(entry.getKey()), 
					generator.simpleMatcher(entry.getValue())))
				.collect(toList());

			List<String> entryStatements = elements.stream()
				.flatMap(pair -> Stream.concat(pair.getElement1().getStatements().stream(), pair.getElement2().getStatements().stream()))
				.collect(toList());

			List<Pair<String, String>> entryValues = elements.stream()
				.map(pair -> new Pair<>(pair.getElement1().getValue(), pair.getElement2().getValue()))
				.collect(toList());

			String containsEntriesMatcher = containsEntriesMatcher(keyType, valueType, entryValues);
			return new Computation(containsEntriesMatcher, parameterized(Matcher.class, null, wildcard()), entryStatements);
		}
	}

}
