package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class BigIntegerSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedImmutable<BigInteger>> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new BigIntegerSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactly(BigInteger.class);
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class);
		value.useAs(BigInteger.class);

		assertThat(value.getUsedTypes()).containsExactly(BigInteger.class);
		assertThat(value.getType()).isEqualTo(BigInteger.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class);
		value.useAs(BigInteger.class);

		serializer.populate(value, BigInteger.valueOf(22));

		assertThat(value.getValue()).isEqualTo(BigInteger.valueOf(22));
	}

}
