package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class BigDecimalSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedImmutable<BigDecimal>> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new BigDecimalSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactly(BigDecimal.class);
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedImmutable<BigDecimal> value = serializer.generate(BigDecimal.class);
		value.useAs(BigDecimal.class);

		assertThat(value.getUsedTypes()).containsExactly(BigDecimal.class);
		assertThat(value.getType()).isEqualTo(BigDecimal.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedImmutable<BigDecimal> value = serializer.generate(BigDecimal.class);
		value.useAs(BigDecimal.class);

		serializer.populate(value, BigDecimal.valueOf(2222, 2));

		assertThat(value.getValue()).isEqualTo(BigDecimal.valueOf(2222, 2));
	}

}
