package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class DefaultLiteralAdaptorTest {

	private AgentConfiguration config;
	private DefaultLiteralAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultLiteralAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAny() throws Exception {
		assertThat(adaptor.matches(int.class)).isTrue();
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedLiteral value = literal("string");
		Deserializer generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("\"string\"");
	}

	private Deserializer generator() {
		return new SetupGenerators(new Adaptors().load(config.loadConfigurations(SetupGenerator.class))).newGenerator(context);
	}

}
