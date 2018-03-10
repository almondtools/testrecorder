package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenEnum;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class DefaultEnumAdaptorTest {

	private AgentConfiguration config;
	private DefaultEnumAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new DefaultEnumAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesOnlyEnums() throws Exception {
		assertThat(adaptor.matches(PublicEnum.class)).isTrue();
		assertThat(adaptor.matches(classOfHiddenEnum())).isTrue();
		assertThat(adaptor.matches(Enum.class)).isFalse();
		assertThat(adaptor.matches(Object.class)).isFalse();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedEnum value = new SerializedEnum(PublicEnum.class);
		value.setName("VALUE1");
		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("sameInstance(PublicEnum.VALUE1)");
	}

	@Test
	public void testTryDeserializeHidden() throws Exception {
		SerializedEnum value = new SerializedEnum(classOfHiddenEnum());
		value.setName("VALUE2");
		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("matchingEnum(\"VALUE2\")");
	}

	@Test
	public void testTryDeserializeHiddenWithGenericResultType() throws Exception {
		SerializedEnum value = new SerializedEnum(classOfHiddenEnum());
		value.useAs(Object.class);
		value.setName("VALUE2");
		MatcherGenerators generator = new MatcherGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("widening(matchingEnum(\"VALUE2\"))");
	}

}
