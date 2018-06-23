package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfCompletelyHidden;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptorTest {

	private AgentConfiguration config;
	private DefaultNullAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultNullAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAny() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedNull value = nullInstance();
		value.useAs(String.class);
		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("nullValue(String.class)");
	}

	@Test
	public void testTryDeserializeOnHidden() throws Exception {
		SerializedNull value = nullInstance();
		value.useAs(Hidden.VisibleInterface.class);

		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(context.getTypes().getImports()).contains("net.amygdalum.testrecorder.util.testobjects.Hidden.VisibleInterface");
		assertThat(result.getValue()).isEqualTo("nullValue(VisibleInterface.class)");
	}

	@Test
	public void testTryDeserializeOnReallyHidden() throws Exception {
		SerializedNull value = nullInstance();
		value.useAs(classOfCompletelyHidden());

		MatcherGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("nullValue()");
	}

	private MatcherGenerators generator() {
		return new MatcherGenerators(new Adaptors<MatcherGenerators>(config).load(MatcherGenerator.class));
	}

}
