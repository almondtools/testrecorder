package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedBigDecimalTest {

	@Test
	public void testGetType() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class);

		assertThat(value.getResultType()).isEqualTo(BigDecimal.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class);

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedImmutable");
	}

}
