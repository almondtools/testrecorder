package net.amygdalum.testrecorder.values;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.Deserializer;

public class SerializedImmutableTest {

	private SerializedImmutable<String> value;

	@Before
	public void before() throws Exception {
		value = new TestImmutable(String.class);
	}

	@Test
	public void testGetResultType() throws Exception {
		assertThat(value.getResultType(), equalTo(String.class));
	}

	@Test
	public void testReferencedValues() throws Exception {
		assertThat(value.referencedValues(), empty());
	}

	@Test
	public void testWithValue() throws Exception {
		value = value.withValue("newvalue");

		assertThat(value.getValue(), equalTo("newvalue"));
	}

	@Test
	public void testSetGetValue() throws Exception {
		value.setValue("value");

		assertThat(value.getValue(), equalTo("value"));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(value.toString(), nullValue());
	}

	private static class TestImmutable extends SerializedImmutable<String> {
		private TestImmutable(Type type) {
			super(type);
		}

		@Override
		public <T> T accept(Deserializer<T> visitor) {
			return null;
		}

	}

}
