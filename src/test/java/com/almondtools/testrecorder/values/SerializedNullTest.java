package com.almondtools.testrecorder.values;

import static com.almondtools.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.testrecorder.visitors.TestValueVisitor;

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

		assertThat(value.accept(new TestValueVisitor()), equalTo("null"));
	}

	@Test
	public void testToString() throws Exception {
		SerializedNull value = nullInstance(String.class);

		assertThat(value.toString(), equalTo("null"));
	}

}
