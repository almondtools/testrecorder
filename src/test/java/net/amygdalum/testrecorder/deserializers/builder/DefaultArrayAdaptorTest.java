package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedArray;

public class DefaultArrayAdaptorTest {

	private DefaultArrayAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultArrayAdaptor();
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
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator, NULL);
		
		assertThat(result.getStatements().toString(), containsString("int[] intArray1 = new int[]{0, 8, 15}"));
		assertThat(result.getValue()).isEqualTo("intArray1");
	}


}
