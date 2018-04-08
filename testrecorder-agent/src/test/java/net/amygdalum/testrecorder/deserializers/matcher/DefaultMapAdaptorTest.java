package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
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
		config = new AgentConfiguration();
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
	public void testTryDeserializeMap() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		MatcherGenerators generator = new MatcherGenerators(config);
		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsEntries(Integer.class, Integer.class)"
			+ ".entry(8, 15)"
			+ ".entry(47, 11)");
	}

	@Test
	public void testTryDeserializeEmptyMap() throws Exception {
		SerializedMap value = new SerializedMap(BigInteger[].class);
		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("noEntries(Object.class, Object.class)");
	}

	@Test
	public void testTryDeserializeGenericComponents() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, parameterized(List.class, null, String.class), parameterized(Set.class, null, String.class)));
		value.put(new SerializedList(String.class).with(literal("str1")), new SerializedSet(String.class).with(literal("str1")));
		value.put(new SerializedList(String.class).with(literal("str2"), literal("str3")), new SerializedSet(String.class).with(literal("str2"), literal("str3")));
		value.put(new SerializedList(String.class), new SerializedSet(String.class));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsEntries((Class<java.util.List<String>>) (Class) java.util.List.class, (Class<java.util.Set<String>>) (Class) java.util.Set.class)"
			+ ".entry(containsInOrder(Object.class, \"str1\"), contains(Object.class, \"str1\"))"
			+ ".entry(containsInOrder(Object.class, \"str2\", \"str3\"), contains(Object.class, \"str2\", \"str3\"))"
			+ ".entry(empty(), empty())");
	}

	@Test
	public void testTryDeserializeHiddenComponents() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Hidden.classOfCompletelyHidden(), Hidden.classOfCompletelyHidden()));
		value.put(new SerializedObject(Hidden.classOfCompletelyHidden()), new SerializedObject(Hidden.classOfCompletelyHidden()));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern(""
			+ "containsEntries(Object.class, Object.class)"
			+ ".entry("
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")),*"
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")))");
	}

}
