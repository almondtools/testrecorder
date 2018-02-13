package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptorTest {

	private DefaultNullAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
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
		assertThat(adaptor.matches(new Object() {}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedNull value = nullInstance(String.class);
		SetupGenerators generator = new SetupGenerators();
		
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("null");
	}


}
