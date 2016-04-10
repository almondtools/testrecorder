package net.amygdalum.testrecorder.serializers;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedImmutable;

@RunWith(MockitoJUnitRunner.class)
public class BigDecimalSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedImmutable<BigDecimal>> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new BigDecimalSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), contains(BigDecimal.class));
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedImmutable<BigDecimal> value = serializer.generate(BigDecimal.class, BigDecimal.class);

		assertThat(value.getResultType(), equalTo(BigDecimal.class));
		assertThat(value.getType(), equalTo(BigDecimal.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedImmutable<BigDecimal> value = serializer.generate(BigDecimal.class, BigDecimal.class);

		serializer.populate(value, BigDecimal.valueOf(2222, 2));

		assertThat(value.getValue(), equalTo(BigDecimal.valueOf(2222, 2)));
	}

}
