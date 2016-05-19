package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static net.amygdalum.testrecorder.deserializers.Templates.containsEntriesMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.noEntriesMatcher;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.deserializers.Adaptor;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultAdaptor;
import net.amygdalum.testrecorder.deserializers.TypeManager;
import net.amygdalum.testrecorder.util.MapMatcher;
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

			Map<Computation, Computation> elements = value.entrySet().stream()
				.collect(toMap(entry -> generator.simpleMatcher(entry.getKey()), entry -> generator.simpleMatcher(entry.getValue())));

			List<String> entryComputations = elements.entrySet().stream()
				.flatMap(entry -> Stream.concat(entry.getKey().getStatements().stream(), entry.getValue().getStatements().stream()))
				.collect(toList());

			Set<Entry<String, String>> entryValues = elements.entrySet().stream()
				.collect(toMap(entry -> entry.getKey().getValue(), entry -> entry.getValue().getValue()))
				.entrySet();

			String containsEntriesMatcher = containsEntriesMatcher(keyType, valueType, entryValues);
			return new Computation(containsEntriesMatcher, parameterized(Matcher.class, null, wildcard()), entryComputations);
		}
	}

}
