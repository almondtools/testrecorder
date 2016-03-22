package net.amygdalum.testrecorder.visitors.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static net.amygdalum.testrecorder.visitors.Templates.containsEntriesMatcher;
import static net.amygdalum.testrecorder.visitors.Templates.noEntriesMatcher;
import static net.amygdalum.testrecorder.visitors.TypeManager.parameterized;
import static net.amygdalum.testrecorder.visitors.TypeManager.wildcard;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.util.MapMatcher;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.visitors.Adaptor;
import net.amygdalum.testrecorder.visitors.Computation;
import net.amygdalum.testrecorder.visitors.DefaultAdaptor;
import net.amygdalum.testrecorder.visitors.TypeManager;

public class DefaultMapAdaptor extends DefaultAdaptor<SerializedMap, ObjectToMatcherCode> implements Adaptor<SerializedMap, ObjectToMatcherCode> {

	@Override
	public Computation tryDeserialize(SerializedMap value, TypeManager types, ObjectToMatcherCode generator) {
		String keyType = types.getSimpleName(value.getMapKeyType());
		String valueType = types.getSimpleName(value.getMapValueType());
		if (value.isEmpty()) {
			types.staticImport(MapMatcher.class, "noEntries");

			String noEntriesMatcher = noEntriesMatcher(keyType, valueType);

			return new Computation(noEntriesMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			types.staticImport(MapMatcher.class, "containsEntries");

			Map<Computation, Computation> elements = value.entrySet().stream()
				.collect(toMap(entry -> generator.simpleValue(entry.getKey()), entry -> generator.simpleValue(entry.getValue())));

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
