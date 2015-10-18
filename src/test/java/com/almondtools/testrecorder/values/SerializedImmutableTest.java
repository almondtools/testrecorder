package com.almondtools.testrecorder.values;

import static com.almondtools.util.objects.EqualityMatcher.satisfiesDefaultEquality;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Test;

import com.almondtools.testrecorder.SerializedValueVisitor;

public class SerializedImmutableTest {

	private SerializedImmutable<String> value;

	@Before
	public void before() throws Exception {
		value = new TestImmutable(String.class);
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(value.getType(), equalTo(String.class));
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

	@Test
	public void testHashCode() throws Exception {
		TestImmutable expected = new TestImmutable(String.class);
		
		assertThat(value.hashCode(), equalTo(expected.hashCode()));
		assertThat(value.withValue("v").hashCode(), equalTo(expected.withValue("v").hashCode()));
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(value, satisfiesDefaultEquality()
			.andEqualTo(new TestImmutable(String.class))
			.andNotEqualTo(new TestImmutable(Void.class))
			.andNotEqualTo(new TestImmutable(String.class).withValue("v"))
			.andNotEqualTo(new SerializedImmutable<String>(String.class) {

				@Override
				public <T> T accept(SerializedValueVisitor<T> visitor) {
					return null;
				}
			}));

		assertThat(value.withValue("v"), satisfiesDefaultEquality()
			.andEqualTo(new TestImmutable(String.class).withValue("v"))
			.andNotEqualTo(new TestImmutable(String.class)));
	}

	private static class TestImmutable extends SerializedImmutable<String> {
		private TestImmutable(Type type) {
			super(type);
		}

		@Override
		public <T> T accept(SerializedValueVisitor<T> visitor) {
			return null;
		}
	}

}
