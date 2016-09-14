package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class DefaultLiteralAdaptorTest {

	private DefaultLiteralAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultLiteralAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesAny() throws Exception {
		assertThat(adaptor.matches(int.class), is(true));
		assertThat(adaptor.matches(Object.class), is(true));
		assertThat(adaptor.matches(new Object() {
		}.getClass()), is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedLiteral value = literal("string");
		MatcherGenerators generator = new MatcherGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements(), empty());
		assertThat(result.getValue(), equalTo("equalTo(\"string\")"));
	}

}
