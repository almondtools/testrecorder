package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptorTest {

	private DefaultSetAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultSetAdaptor();
	}
	
	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object(){}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeSet() throws Exception {
		SerializedSet value = new SerializedSet(BigInteger[].class);
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(0)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(8)));
		value.add(new SerializedImmutable<>(BigInteger.class).withValue(BigInteger.valueOf(15)));

		MatcherGenerators generator = new MatcherGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator, NULL);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue()).isEqualTo("contains(Object.class, equalTo(new BigInteger(\"0\")), equalTo(new BigInteger(\"8\")), equalTo(new BigInteger(\"15\")))");
	}

	@Test
	public void testTryDeserializeEmptySet() throws Exception {
		SerializedSet value = new SerializedSet(BigInteger[].class);
		MatcherGenerators generator = new MatcherGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator, NULL);
		
		assertThat(result.getStatements(), empty());
		assertThat(result.getValue()).isEqualTo("empty()");
	}

}
