package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultClassAdaptorTest {

	private AgentConfiguration config;
	private DefaultClassAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultClassAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesOnlyClass() throws Exception {
		assertThat(adaptor.matches(Class.class)).isTrue();
		assertThat(adaptor.matches(Object.class)).isFalse();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedImmutable<Class<?>> value = new SerializedImmutable<>(Class.class);
		value.setValue(BigDecimal.class);
		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("equalTo(java.math.BigDecimal.class)");
	}

	private MatcherGenerators generator() {
		return new MatcherGenerators(new Adaptors<MatcherGenerators>(config).load(MatcherGenerator.class));
	}

}
