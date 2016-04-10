package net.amygdalum.testrecorder.serializers;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedImmutable;

@RunWith(MockitoJUnitRunner.class)
public class BigIntegerSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedImmutable<BigInteger>> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new BigIntegerSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), contains(BigInteger.class));
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class, BigInteger.class);

		assertThat(value.getType(), equalTo(BigInteger.class));
		assertThat(value.getValueType(), equalTo(BigInteger.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class, BigInteger.class);

		serializer.populate(value, BigInteger.valueOf(22));

		assertThat(value.getValue(), equalTo(BigInteger.valueOf(22)));
	}

}
