package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class BigDecimalSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedImmutable<BigDecimal>> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new BigDecimalSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactly(BigDecimal.class);
	}

	@Test
	void testGenerate() throws Exception {
		SerializedImmutable<BigDecimal> value = serializer.generate(BigDecimal.class, session);
		value.useAs(BigDecimal.class);

		assertThat(value.getUsedTypes()).containsExactly(BigDecimal.class);
		assertThat(value.getType()).isEqualTo(BigDecimal.class);
	}

	@Test
	void testPopulate() throws Exception {
		SerializedImmutable<BigDecimal> value = serializer.generate(BigDecimal.class, session);
		value.useAs(BigDecimal.class);

		serializer.populate(value, BigDecimal.valueOf(2222, 2), session);

		assertThat(value.getValue()).isEqualTo(BigDecimal.valueOf(2222, 2));
	}

}
