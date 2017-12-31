package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedBigIntegerTest {

	@Test
	public void testGetType() throws Exception {
		SerializedImmutable<BigInteger> value = new SerializedImmutable<BigInteger>(BigInteger.class);

		assertThat(value.getResultType()).isEqualTo(BigInteger.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedImmutable<BigInteger> value = new SerializedImmutable<BigInteger>(BigInteger.class);

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedImmutable");
	}

}
