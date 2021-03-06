package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedBigDecimalTest {

	@Test
	public void testGetUsedTypes() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class);

		assertThat(value.getUsedTypes()).containsExactly(BigDecimal.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<BigDecimal>(BigDecimal.class);

		assertThat(value.accept(new TestValueVisitor())).isEqualTo("ImmutableType:SerializedImmutable");
	}

}
