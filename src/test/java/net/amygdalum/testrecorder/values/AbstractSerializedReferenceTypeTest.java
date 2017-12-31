package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.SerializedValues.ASerializedReferenceType;

public class AbstractSerializedReferenceTypeTest {

	@Test
	public void testASerializedValue() throws Exception {
		ASerializedReferenceType value = new ASerializedReferenceType(String.class);

		assertThat(value.getType()).isSameAs(String.class);
		assertThat(value.getResultType()).isSameAs(String.class);
	}

	@Test
	public void testSetResultType() throws Exception {
		ASerializedReferenceType value = new ASerializedReferenceType(String.class);
		value.setResultType(Object.class);
		
		assertThat(value.getType()).isSameAs(String.class);
		assertThat(value.getResultType()).isSameAs(Object.class);
	}

	@Test
	public void testGetId() throws Exception {
		ASerializedReferenceType value = new ASerializedReferenceType(String.class);
		value.setId(33);
		
		assertThat(value.getId()).isEqualTo(33);
	}

}
