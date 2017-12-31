package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultBigIntegerAdaptorTest {

	private DefaultBigIntegerAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultBigIntegerAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesOnlyBigInteger() throws Exception {
		assertThat(adaptor.matches(BigInteger.class)).isTrue();
		assertThat(adaptor.matches(Object.class)).isFalse();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedImmutable<BigInteger> value = new SerializedImmutable<>(BigInteger.class);
		value.setValue(new BigInteger("0815"));
		MatcherGenerators generator = new MatcherGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("equalTo(new BigInteger(\"815\"))");
	}

}
