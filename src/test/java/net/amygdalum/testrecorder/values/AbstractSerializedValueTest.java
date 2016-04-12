package net.amygdalum.testrecorder.values;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;

import org.junit.Test;

import net.amygdalum.testrecorder.Deserializer;

public class AbstractSerializedValueTest {

	@Test
	public void testASerializedValue() throws Exception {
		ASerializedValue value = new ASerializedValue(String.class);

		assertThat(value.getType(), sameInstance(String.class));
		assertThat(value.getResultType(), sameInstance(String.class));
	}

	@Test
	public void testSetType() throws Exception {
		ASerializedValue value = new ASerializedValue(String.class);
		value.setType(Object.class);
		
		assertThat(value.getType(), sameInstance(Object.class));
		assertThat(value.getResultType(), sameInstance(Object.class));
	}

	private static class ASerializedValue extends AbstractSerializedValue {
		public ASerializedValue(Type type) {
			super(type);
		}

		@Override
		public <T> T accept(Deserializer<T> visitor) {
			return null;
		}
	}

}
