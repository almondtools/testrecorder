package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedNullTest {

	@Test
	public void testLiteral() throws Exception {
		SerializedNull value = nullInstance(String.class);
		SerializedNull testvalue = nullInstance(String.class);

		assertThat(testvalue, sameInstance(value));
	}

	@Test
	public void testGetResultType() throws Exception {
		SerializedNull value = nullInstance(String.class);

		assertThat(value.getResultType()).isEqualTo(String.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedNull value = nullInstance(String.class);

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedNull");
	}

	@Test
	public void testToString() throws Exception {
		SerializedNull value = nullInstance(String.class);

		assertThat(value.toString()).isEqualTo("null");
	}

}
