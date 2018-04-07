package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class AbstractSerializedReferenceTypeTest {

	@Test
	public void testASerializedValue() throws Exception {
		ASerializedReferenceType value = new ASerializedReferenceType(String.class);

		assertThat(value.getType()).isSameAs(String.class);
		assertThat(value.getUsedTypes()).containsExactly(String.class);
	}

	@Test
		public void testUseAs() throws Exception {
			ASerializedReferenceType value = new ASerializedReferenceType(String.class);
			value.useAs(Object.class);
			
			assertThat(value.getType()).isSameAs(String.class);
			assertThat(value.getUsedTypes()).containsExactly(Object.class);
		}

	@Test
	public void testGetId() throws Exception {
		ASerializedReferenceType value = new ASerializedReferenceType(String.class);
		value.setId(33);
		
		assertThat(value.getId()).isEqualTo(33);
	}

}
