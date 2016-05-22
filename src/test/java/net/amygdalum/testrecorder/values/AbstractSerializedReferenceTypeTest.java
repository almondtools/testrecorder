package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;

public class AbstractSerializedReferenceTypeTest {

	@Test
	public void testASerializedValue() throws Exception {
		ASerializedReferenceType value = new ASerializedReferenceType(String.class);

		assertThat(value.getType(), sameInstance(String.class));
		assertThat(value.getResultType(), sameInstance(String.class));
	}

	@Test
	public void testSetResultType() throws Exception {
		ASerializedReferenceType value = new ASerializedReferenceType(String.class);
		value.setResultType(Object.class);
		
		assertThat(value.getType(), sameInstance(String.class));
		assertThat(value.getResultType(), sameInstance(Object.class));
	}

	private static class ASerializedReferenceType extends AbstractSerializedReferenceType {
		public ASerializedReferenceType(Type type) {
			super(type);
		}
		
		@Override
		public List<SerializedValue> referencedValues() {
			return emptyList();
		}

		@Override
		public <T> T accept(Deserializer<T> visitor) {
			return null;
		}
	}

}
