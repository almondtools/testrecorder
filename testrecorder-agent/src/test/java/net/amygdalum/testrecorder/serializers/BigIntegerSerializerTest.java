package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class BigIntegerSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedImmutable<BigInteger>> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new BigIntegerSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactly(BigInteger.class);
	}

	@Test
	void testGenerate() throws Exception {
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class, session);
		value.useAs(BigInteger.class);

		assertThat(value.getUsedTypes()).containsExactly(BigInteger.class);
		assertThat(value.getType()).isEqualTo(BigInteger.class);
	}

	@Test
	void testPopulate() throws Exception {
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class, session);
		value.useAs(BigInteger.class);

		serializer.populate(value, BigInteger.valueOf(22), session);

		assertThat(value.getValue()).isEqualTo(BigInteger.valueOf(22));
	}

}
