package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultArrayAdaptorTest {

	private AgentConfiguration config;
	private DefaultArrayAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultArrayAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(int[].class)).isTrue();
		assertThat(adaptor.matches(Object[].class)).isTrue();
		assertThat(adaptor.matches(Integer[].class)).isTrue();
	}

	@Test
	public void testTryDeserializePrimitiveArray() throws Exception {
		SerializedArray value = new SerializedArray(int[].class);
		value.add(literal(int.class, 0));
		value.add(literal(int.class, 8));
		value.add(literal(int.class, 15));
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("intArrayContaining(0, 8, 15)");
	}

	@Test
	public void testTryDeserializePrimitiveEmptyArray() throws Exception {
		SerializedArray value = new SerializedArray(int[].class);
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("intEmptyArray()");
	}

	@Test
	public void testTryDeserializeObjectArray() throws Exception {
		SerializedArray value = new SerializedArray(BigInteger[].class);
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));
		Deserializer generator = generator();
		context.getTypes().registerTypes(BigInteger.class);

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("arrayContaining(BigInteger.class, equalTo(new BigInteger(\"0\")), equalTo(new BigInteger(\"8\")), equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeEmptyObjectArray() throws Exception {
		SerializedArray value = new SerializedArray(BigInteger[].class);
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("emptyArray()");
	}

	private Deserializer generator() {
		return new MatcherGenerators(new Adaptors().load(config.loadConfigurations(MatcherGenerator.class))).newGenerator(context);
	}

}
