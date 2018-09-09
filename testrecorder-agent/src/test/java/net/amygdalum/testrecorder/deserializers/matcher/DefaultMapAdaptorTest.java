package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedMap;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultMapAdaptorTest {

	private AgentConfiguration config;
	private DefaultMapAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultMapAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeExplicitelyTypedMap() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		Deserializer generator = generator();
		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsEntries(Integer.class, Integer.class)"
			+ ".entry(8, 15)"
			+ ".entry(47, 11)");
	}

	@Test
	public void testTryDeserializeMap() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		Deserializer generator = generator();
		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsEntries(Integer.class, Integer.class)"
			+ ".entry(8, 15)"
			+ ".entry(47, 11)");
	}

	@Test
	public void testTryDeserializeRawMap() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		Deserializer generator = generator();
		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsEntries()"
			+ ".entry(8, 15)"
			+ ".entry(47, 11)");
	}

	@Test
	public void testTryDeserializeEmptyMap() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(parameterized(Map.class, null, BigInteger.class, String.class));
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("noEntries(BigInteger.class, String.class)");
	}

	@Test
	public void testTryDeserializeEmptyRawMap() throws Exception {
		SerializedMap value = new SerializedMap(Map.class);
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("noEntries()");
	}

	@Test
	public void testTryDeserializeGenericComponents() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(LinkedHashMap.class, null, parameterized(List.class, null, String.class), parameterized(Set.class, null, String.class)));
		value.put(list(parameterized(List.class, null, String.class), literal("str1")),
			set(parameterized(Set.class, null, String.class), literal("str1")));
		value.put(list(parameterized(List.class, null, String.class), literal("str2"), literal("str3")),
			set(parameterized(Set.class, null, String.class), literal("str2"), literal("str3")));
		value.put(list(parameterized(List.class, null, String.class)),
			set(parameterized(Set.class, null, String.class)));

		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsEntries((Class<List<String>>) (Class) List.class, (Class<Set<String>>) (Class) Set.class)"
			+ ".entry(containsInOrder(String.class, \"str1\"), contains(String.class, \"str1\"))"
			+ ".entry(containsInOrder(String.class, \"str2\", \"str3\"), contains(String.class, \"str2\", \"str3\"))"
			+ ".entry(empty(String.class), empty(String.class))");
	}

	@Test
	public void testTryDeserializeHiddenComponents() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(LinkedHashMap.class, null, Hidden.classOfCompletelyHidden(), Hidden.classOfCompletelyHidden()));
		value.put(new SerializedObject(Hidden.classOfCompletelyHidden()), new SerializedObject(Hidden.classOfCompletelyHidden()));

		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern(""
			+ "containsEntries(Object.class, Object.class)"
			+ ".entry("
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")),*"
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")))");
	}

	private SerializedList list(Type type, SerializedValue... values) {
		SerializedList list = new SerializedList(baseType(type));
		list.useAs(type);
		for (SerializedValue value : values) {
			list.add(value);
		}
		return list;
	}

	private SerializedSet set(Type type, SerializedValue... values) {
		SerializedSet set = new SerializedSet(baseType(type));
		set.useAs(type);
		for (SerializedValue value : values) {
			set.add(value);
		}
		return set;
	}

	private Deserializer generator() {
		return new MatcherGenerators(new Adaptors(config).load(MatcherGenerator.class)).newGenerator(context);
	}

}
