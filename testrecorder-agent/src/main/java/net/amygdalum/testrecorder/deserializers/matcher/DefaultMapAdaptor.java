package net.amygdalum.testrecorder.deserializers.matcher;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.deserializers.Templates.cast;
import static net.amygdalum.testrecorder.deserializers.Templates.containsEntriesMatcher;
import static net.amygdalum.testrecorder.deserializers.Templates.noEntriesMatcher;
import static net.amygdalum.testrecorder.types.Computation.expression;
import static net.amygdalum.testrecorder.util.Types.assignableTypes;
import static net.amygdalum.testrecorder.util.Types.isGeneric;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.hamcrest.Matcher;

import net.amygdalum.testrecorder.runtime.MapMatcher;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.Pair;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultMapAdaptor extends DefaultMatcherGenerator<SerializedMap> implements MatcherGenerator<SerializedMap> {

	@Override
	public Class<SerializedMap> getAdaptedClass() {
		return SerializedMap.class;
	}

	@Override
	public Computation tryDeserialize(SerializedMap value, MatcherGenerators generator, DeserializerContext context) {
		Type mapKeyType = value.getMapKeyType();
		Type mapValueType = value.getMapValueType();

		TypeManager types = context.getTypes();
		if (types.isHidden(mapKeyType)) {
			mapKeyType = Object.class;
		}
		if (types.isHidden(mapValueType)) {
			mapValueType = Object.class;
		}

		String keyType = types.getRawClass(mapKeyType);
		if (isGeneric(mapKeyType)) {
			keyType = context.adapt(keyType, parameterized(Class.class, null, mapKeyType), parameterized(Class.class, null, wildcard()));
		}
		String valueType = types.getRawClass(mapValueType);
		if (isGeneric(mapValueType)) {
			valueType = context.adapt(valueType, parameterized(Class.class, null, mapValueType), parameterized(Class.class, null, wildcard()));
		}

		if (value.isEmpty()) {
			types.staticImport(MapMatcher.class, "noEntries");

			String noEntriesMatcher = noEntriesMatcher(keyType, valueType);

			return expression(noEntriesMatcher, parameterized(Matcher.class, null, wildcard()), emptyList());
		} else {
			types.staticImport(MapMatcher.class, "containsEntries");
			types.registerTypes(mapKeyType, mapValueType);

			EntryDeserializer deserializer = new EntryDeserializer(generator, context, mapKeyType, mapValueType);
			List<Pair<Computation, Computation>> elements = value.entrySet().stream()
				.map(deserializer::computeKeyValues)
				.collect(toList());

			List<String> entryStatements = elements.stream()
				.flatMap(pair -> Stream.concat(pair.getElement1().getStatements().stream(), pair.getElement2().getStatements().stream()))
				.collect(toList());

			List<Pair<String, String>> entryValues = elements.stream()
				.map(pair -> new Pair<>(pair.getElement1().getValue(), pair.getElement2().getValue()))
				.collect(toList());

			String containsEntriesMatcher = containsEntriesMatcher(keyType, valueType, entryValues);
			return expression(containsEntriesMatcher, parameterized(Matcher.class, null, wildcard()), entryStatements);
		}
	}

	private static class EntryDeserializer {

		private MatcherGenerators generator;
		private DeserializerContext context;
		private Type mapKeyType;
		private Type mapValueType;

		public EntryDeserializer(MatcherGenerators generator, DeserializerContext context, Type mapKeyType, Type mapValueType) {
			this.generator = generator;
			this.context = context;
			this.mapKeyType = mapKeyType;
			this.mapValueType = mapValueType;
		}

		private Pair<Computation, Computation> computeKeyValues(Entry<SerializedValue, SerializedValue> entry) {
			TypeManager types = context.getTypes();

			SerializedValue key = entry.getKey();
			SerializedValue value = entry.getValue();

			Computation keyDeserialized = generator.simpleMatcher(key, context);

			Computation valueDeserialized = generator.simpleMatcher(value, context);

			Type keyType = key instanceof SerializedNull ? null : types.mostSpecialOf(key.getUsedTypes()).orElse(Object.class);
			Type valueType = value instanceof SerializedNull ? null : types.mostSpecialOf(value.getUsedTypes()).orElse(Object.class);

			if (assignableTypes(mapKeyType, keyType) && assignableTypes(Matcher.class, keyType)) {
				String keyTypeName = types.getRawTypeName(mapKeyType);
				keyDeserialized = new Computation(cast(keyTypeName, keyDeserialized.getValue()), keyDeserialized.getType(), false, keyDeserialized.getStatements());
			}
			if (assignableTypes(mapValueType, valueType) && assignableTypes(Matcher.class, valueType)) {
				String valueTypeName = types.getRawTypeName(mapValueType);
				valueDeserialized = new Computation(cast(valueTypeName, valueDeserialized.getValue()), valueDeserialized.getType(), false, valueDeserialized.getStatements());
			}

			return new Pair<>(keyDeserialized, valueDeserialized);
		}

	}

}
