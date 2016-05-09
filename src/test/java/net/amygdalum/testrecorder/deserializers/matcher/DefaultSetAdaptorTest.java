package net.amygdalum.testrecorder.deserializers.matcher;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptorTest {

	private DefaultSetAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultSetAdaptor();
	}
	
	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(Object.class),is(true));
		assertThat(adaptor.matches(new Object(){}.getClass()),is(true));
	}

	@Test
	public void testTryDeserializeSet() throws Exception {
		SerializedSet value = new SerializedSet(BigInteger[].class);
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));

		ObjectToMatcherCode generator = new ObjectToMatcherCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("containsInAnyOrder(equalTo(new BigInteger(\"0\")), equalTo(new BigInteger(\"8\")), equalTo(new BigInteger(\"15\")))"));
	}

	@Test
	public void testTryDeserializeEmptySet() throws Exception {
		SerializedSet value = new SerializedSet(BigInteger[].class);
		ObjectToMatcherCode generator = new ObjectToMatcherCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("empty()"));
	}

}
