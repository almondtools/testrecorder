package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedBigDecimalTest {

	@Test
	public void testGetType() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class);

		assertThat(value.getResultType(), equalTo(BigDecimal.class));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class);

		assertThat(value.accept(new TestValueVisitor(), NULL), equalTo("SerializedImmutable"));
	}

}
