package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListAdaptorTest {

	private AgentConfiguration config;
	private ArraysListAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new ArraysListAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isSameAs(DefaultListAdaptor.class);
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(Class.forName("java.util.Arrays$ArrayList"))).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedList value = listOf("java.util.Arrays$ArrayList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"Integer[] integerArray1 = new Integer[]{0, 8, 15}",
			"List<Integer> list1 = asList(integerArray1)");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	private SerializedList listOf(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className));
		value.useAs(parameterized(List.class, null, Integer.class));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

}
