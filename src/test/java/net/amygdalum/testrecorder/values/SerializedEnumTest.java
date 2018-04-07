package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedEnumTest {

	@Test
	public void testGetType() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class).withName("VALUE1");

		assertThat(value.getUsedTypes()).containsExactly(MyEnum.class);
		assertThat(value.getName()).isEqualTo("VALUE1");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedEnum");
	}

	@Test
	public void testSetGetName() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);
		
		value.setName("VALUE2");
		
		assertThat(value.getName()).isEqualTo("VALUE2");
	}

	@Test
	public void testReferencedValues() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);
		
		value.setName("VALUE2");

		assertThat(value.referencedValues()).isEmpty();
	}

	@Test
	public void testToString() throws Exception {
		SerializedEnum value= new SerializedEnum(MyEnum.class);
		value.setName("VALUE1");

		assertThat(value.toString()).isEqualTo("VALUE1");
	}


	private static enum MyEnum {
		VALUE1, VALUE2;
	}


}
