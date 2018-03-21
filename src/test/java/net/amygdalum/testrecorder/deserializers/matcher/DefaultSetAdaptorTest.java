package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptorTest {

	private AgentConfiguration config;
	private DefaultSetAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new DefaultSetAdaptor();
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
	public void testTryDeserializeSet() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, BigInteger.class));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "contains(java.math.BigInteger.class, "
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeEmptySet() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, BigInteger.class));
		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("empty()");
	}

	@Test
	public void testTryDeserializeGenericComponents() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, parameterized(Set.class, null, String.class)));
		value.add(new SerializedSet(String.class).with(literal("str1")));
		value.add(new SerializedSet(String.class).with(literal("str2"), literal("str3")));
		value.add(new SerializedSet(String.class));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "contains((Class<java.util.Set<String>>) (Class) java.util.Set.class, "
			+ "contains(Object.class, \"str1\"), "
			+ "contains(Object.class, \"str2\", \"str3\"), "
			+ "empty())");
	}

	@Test
	public void testTryDeserializeHiddenComponents() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, Hidden.classOfCompletelyHidden()));
		value.add(new SerializedObject(Hidden.classOfCompletelyHidden()));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern(""
			+ "contains(Object.class, "
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")))");
	}

}
