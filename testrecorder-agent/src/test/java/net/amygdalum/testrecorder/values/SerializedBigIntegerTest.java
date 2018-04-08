package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedBigIntegerTest {

	@Test
	public void testGetType() throws Exception {
		SerializedImmutable<BigInteger> value = new SerializedImmutable<BigInteger>(BigInteger.class);

		assertThat(value.getUsedTypes()).containsExactly(BigInteger.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedImmutable<BigInteger> value = new SerializedImmutable<BigInteger>(BigInteger.class);

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedImmutable");
	}

}
