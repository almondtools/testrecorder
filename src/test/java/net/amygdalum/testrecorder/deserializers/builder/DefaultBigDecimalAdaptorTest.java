package net.amygdalum.testrecorder.deserializers.builder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigDecimalAdaptorTest {

	private DefaultBigDecimalAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultBigDecimalAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesOnlyBigDecimal() throws Exception {
		assertThat(adaptor.matches(BigDecimal.class), is(true));
		assertThat(adaptor.matches(Object.class), is(false));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedImmutable<BigDecimal> value = new SerializedImmutable<>(BigDecimal.class);
		value.setValue(new BigDecimal("0.815"));
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("new BigDecimal(\"0.815\")"));
	}

}
