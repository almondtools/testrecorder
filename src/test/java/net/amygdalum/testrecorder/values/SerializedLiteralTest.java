package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.deserializers.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.isLiteral;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Collection;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedLiteralTest {

	@Test
	public void testIsLiteral() throws Exception {
		assertThat(isLiteral(boolean.class), is(true));
		assertThat(isLiteral(Boolean.class), is(true));
		assertThat(isLiteral(int.class), is(true));
		assertThat(isLiteral(Integer.class), is(true));
		assertThat(isLiteral(String.class), is(true));

		assertThat(isLiteral(Object.class), is(false));
		assertThat(isLiteral(BigDecimal.class), is(false));
		assertThat(isLiteral(Collection.class), is(false));
	}

	@Test
	public void testLiteral() throws Exception {
		SerializedLiteral value = literal("string");
		SerializedLiteral testvalue = literal("string");

		assertThat(testvalue, sameInstance(value));
	}

	@Test
	public void testGetResultType() throws Exception {
		SerializedLiteral value = literal("string");

		assertThat(value.getResultType(), equalTo(String.class));
	}

	@Test
	public void testGetValue() throws Exception {
		SerializedLiteral value = literal("string");

		assertThat(value.getValue(), equalTo("string"));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedLiteral value = literal("string");

		assertThat(value.accept(new TestValueVisitor(), NULL), equalTo("SerializedLiteral"));
	}

	@Test
	public void testToString() throws Exception {
		SerializedLiteral value = literal("string");

		assertThat(value.toString(), equalTo("string"));
	}

}
