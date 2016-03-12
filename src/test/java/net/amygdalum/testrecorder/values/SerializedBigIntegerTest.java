package net.amygdalum.testrecorder.values;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Test;

import net.amygdalum.testrecorder.visitors.TestValueVisitor;

import net.amygdalum.testrecorder.values.SerializedBigInteger;

public class SerializedBigIntegerTest {

	@Test
	public void testGetType() throws Exception {
		SerializedBigInteger value = new SerializedBigInteger(BigInteger.class, BigInteger.class);

		assertThat(value.getType(), equalTo(BigInteger.class));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedBigInteger value = new SerializedBigInteger(BigInteger.class, BigInteger.class);

		assertThat(value.accept(new TestValueVisitor()), equalTo("unknown"));
		assertThat(value.accept(new TestImmutableVisitor()), equalTo("bigInteger"));
	}

}
