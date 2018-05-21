package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
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
	public void testTryDeserializeExplicitelyTypedSet() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, BigInteger.class));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));

		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "contains(BigInteger.class, "
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeSet() throws Exception {
		SerializedSet value = new SerializedSet(Set.class);
		value.useAs(parameterized(Set.class, null, BigInteger.class));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));
		
		MatcherGenerators generator = generator();
		
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "contains(BigInteger.class, "
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}
	
	@Test
	public void testTryDeserializeRawSet() throws Exception {
		SerializedSet value = new SerializedSet(Set.class);
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));
		
		MatcherGenerators generator = generator();
		
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "contains("
			+ "equalTo(new BigInteger(\"0\")), "
			+ "equalTo(new BigInteger(\"8\")), "
			+ "equalTo(new BigInteger(\"15\")))");
	}
	
	@Test
	public void testTryDeserializeEmptySet() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, BigInteger.class));
		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("empty(BigInteger.class)");
	}

	@Test
	public void testTryDeserializeEmptyRawSet() throws Exception {
		SerializedSet value = new SerializedSet(Set.class);
		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("empty()");
	}

	@Test
	public void testTryDeserializeGenericComponents() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, parameterized(Set.class, null, String.class)));
		value.add(new SerializedSet(parameterized(Set.class, null, String.class)).with(literal("str1")));
		value.add(new SerializedSet(parameterized(Set.class, null, String.class)).with(literal("str2"), literal("str3")));
		value.add(new SerializedSet(parameterized(Set.class, null, String.class)));

		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo(""
			+ "contains((Class<Set<String>>) (Class) Set.class, "
			+ "contains(String.class, \"str1\"), "
			+ "contains(String.class, \"str2\", \"str3\"), "
			+ "empty(String.class))");
	}

	@Test
	public void testTryDeserializeHiddenComponents() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(Set.class, null, Hidden.classOfCompletelyHidden()));
		value.add(new SerializedObject(Hidden.classOfCompletelyHidden()));

		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern(""
			+ "contains(Object.class, "
			+ "new GenericMatcher() {*}.matching(clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$CompletelyHidden\")))");
	}

	private MatcherGenerators generator() {
		return new MatcherGenerators(new Adaptors<MatcherGenerators>(config).load(MatcherGenerator.class));
	}

}
