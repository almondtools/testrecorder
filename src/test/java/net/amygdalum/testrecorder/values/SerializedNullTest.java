package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

import net.amygdalum.testrecorder.values.SerializedNull;

public class SerializedNullTest {

	@Test
	public void testLiteral() throws Exception {
		SerializedNull value = nullInstance(String.class);
		SerializedNull testvalue = nullInstance(String.class);

		assertThat(testvalue, sameInstance(value));
	}

	@Test
	public void testGetType() throws Exception {
		SerializedNull value = nullInstance(String.class);

		assertThat(value.getType(), equalTo(String.class));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedNull value = nullInstance(String.class);

		assertThat(value.accept(new TestValueVisitor()), equalTo("SerializedNull"));
	}

	@Test
	public void testToString() throws Exception {
		SerializedNull value = nullInstance(String.class);

		assertThat(value.toString(), equalTo("null"));
	}

}
