package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedNull;

public class DefaultNullAdaptorTest {

	private DefaultNullAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultNullAdaptor();
	}
	
	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesAny() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedNull value = nullInstance(String.class);
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator, NULL);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue()).isEqualTo("null");
	}


}
