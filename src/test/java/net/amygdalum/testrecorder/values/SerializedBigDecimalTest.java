package net.amygdalum.testrecorder.values;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

import net.amygdalum.testrecorder.visitors.TestValueVisitor;

import net.amygdalum.testrecorder.values.SerializedBigDecimal;

public class SerializedBigDecimalTest {

	@Test
	public void testGetType() throws Exception {
		SerializedBigDecimal value = new SerializedBigDecimal(BigDecimal.class, BigDecimal.class);

		assertThat(value.getType(), equalTo(BigDecimal.class));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedBigDecimal value = new SerializedBigDecimal(BigDecimal.class, BigDecimal.class);

		assertThat(value.accept(new TestValueVisitor()), equalTo("unknown"));
		assertThat(value.accept(new TestImmutableVisitor()), equalTo("bigDecimal"));
	}

}
