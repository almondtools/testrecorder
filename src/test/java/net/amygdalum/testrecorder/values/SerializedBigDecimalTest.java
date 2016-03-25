package net.amygdalum.testrecorder.values;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

import net.amygdalum.testrecorder.values.SerializedImmutable;

public class SerializedBigDecimalTest {

	@Test
	public void testGetType() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class, BigDecimal.class);

		assertThat(value.getType(), equalTo(BigDecimal.class));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class, BigDecimal.class);

		assertThat(value.accept(new TestValueVisitor()), equalTo("SerializedImmutable"));
	}

}
