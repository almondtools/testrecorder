package net.amygdalum.testrecorder.values;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedBigIntegerTest {

	@Test
	public void testGetType() throws Exception {
		SerializedImmutable<BigInteger> value = new SerializedImmutable<BigInteger>(BigInteger.class, BigInteger.class);

		assertThat(value.getType(), equalTo(BigInteger.class));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedImmutable<BigInteger> value = new SerializedImmutable<BigInteger>(BigInteger.class, BigInteger.class);

		assertThat(value.accept(new TestValueVisitor()), equalTo("SerializedImmutable"));
	}

}
