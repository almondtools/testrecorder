package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class DefaultSequenceAdaptorTest {

	private AgentConfiguration config;
	private DefaultSequenceAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new DefaultSequenceAdaptor();
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
	public void testTryDeserializeList() throws Exception {
		SerializedList value = new SerializedList(parameterized(List.class, null, BigInteger.class));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));
		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsInOrder(java.math.BigInteger.class, "
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeEmptyList() throws Exception {
		SerializedList value = new SerializedList(parameterized(List.class, null, BigInteger.class));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("empty()");
	}

	@Test
	public void testTryDeserializeGenericComponents() throws Exception {
		SerializedList value = new SerializedList(parameterized(List.class, null, parameterized(List.class, null, String.class)));
		value.add(new SerializedList(String.class).with(literal("str1")));
		value.add(new SerializedList(String.class).with(literal("str2"), literal("str3")));
		value.add(new SerializedList(String.class));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "containsInOrder((Class<java.util.List<String>>) (Class) java.util.List.class, "
			+ "containsInOrder(Object.class, \"str1\"), "
			+ "containsInOrder(Object.class, \"str2\", \"str3\"), "
			+ "empty())");
	}

	@Test
	public void testTryDeserializeHiddenComponents() throws Exception {
		SerializedList value = new SerializedList(parameterized(List.class, null, Hidden.classOfCompletelyHidden()));
		value.add(new SerializedObject(Hidden.classOfCompletelyHidden()));

		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern(""
			+ "containsInOrder(Object.class, "
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")))");
	}

}
