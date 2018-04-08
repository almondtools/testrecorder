package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedArray;

public class DefaultArrayAdaptorTest {

	private AgentConfiguration config;
	private DefaultArrayAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
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
	public void testTryDeserialize() throws Exception {
		SerializedArray value = new SerializedArray(int[].class);
		value.add(literal(int.class, 0));
		value.add(literal(int.class, 8));
		value.add(literal(int.class, 15));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).contains("int[] intArray1 = new int[]{0, 8, 15}");
		assertThat(result.getValue()).isEqualTo("intArray1");
	}

}
