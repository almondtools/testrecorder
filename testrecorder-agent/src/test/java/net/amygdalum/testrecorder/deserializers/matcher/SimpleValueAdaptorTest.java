package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.SerializedValues;
import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.Dubble;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class SimpleValueAdaptorTest {

	private AgentConfiguration config;
	private SimpleValueAdaptor adaptor;
	private DeserializerContext context;
	private SerializedValues values;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new SimpleValueAdaptor();
		context = new DefaultDeserializerContext();
		values = new SerializedValues(config);
	}

	@Test
	void testNullIsSimpleValue() throws Exception {
		Deserializer generator = generator();

		assertThat(adaptor.isSimpleValue(nullInstance())).isTrue();
		assertThat(adaptor.tryDeserialize(nullInstance(), generator).getStatements()).isEmpty();
		assertThat(adaptor.tryDeserialize(nullInstance(), generator).getValue()).isEqualTo("null");
	}

	@Test
	void testLiteralIsSimpleValue() throws Exception {
		Deserializer generator = generator();

		assertThat(adaptor.isSimpleValue(literal("str"))).isTrue();
		assertThat(adaptor.tryDeserialize(literal("str"), generator).getStatements()).isEmpty();
		assertThat(adaptor.tryDeserialize(literal("str"), generator).getValue()).isEqualTo("\"str\"");
	}

	@Test
	void testOtherIsNotSimpleValue() throws Exception {
		Deserializer generator = generator();

		assertThat(adaptor.isSimpleValue(values.object(Dubble.class, new Dubble("Foo", "Bar")))).isFalse();
		assertThat(adaptor.tryDeserialize(values.object(Dubble.class, new Dubble("Foo", "Bar")), generator).getStatements()).isEmpty();
		assertThat(adaptor.tryDeserialize(values.object(Dubble.class, new Dubble("Foo", "Bar")), generator).getValue()).containsWildcardPattern("new GenericMatcher() {*"
			+ "a = \"Foo\"*"
			+ "b = \"Bar\"*"
			+ "}.matching(Dubble.class)");
	}

	@Test
	void testSimpleMatcherSerializedValueNull() throws Exception {
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(nullInstance(), generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).as("generic matchers can match nulls and do not need matchers here").isEqualTo("null");
	}

	@Test
	void testSimpleMatcherSerializedValueLiteral() throws Exception {
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(literal("string"), generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).as("generic matchers can match literals and do not need matchers here").isEqualTo("\"string\"");
	}

	@Test
	void testSimpleMatcherSerializedValueObject() throws Exception {
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(values.object(Simple.class, new Simple()), generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).containsWildcardPattern("new GenericMatcher() {*String str = null;*}.matching(Simple.class)");
	}

	private Deserializer generator() {
		return new MatcherGenerators(new Adaptors(config).load(MatcherGenerator.class)).newGenerator(context);
	}

}
