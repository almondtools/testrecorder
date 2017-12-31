package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class DefaultLiteralAdaptorTest {

	private DefaultLiteralAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultLiteralAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesAny() throws Exception {
		assertThat(adaptor.matches(int.class)).isTrue();
		assertThat(adaptor.matches(Object.class)).isTrue();
		assertThat(adaptor.matches(new Object() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedLiteral value = literal("string");
		MatcherGenerators generator = new MatcherGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue()).isEqualTo("equalTo(\"string\")");
	}

}
