package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedEnumTest {

	@Test
	public void testGetUsedType() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class).withName("VALUE1");

		assertThat(value.getUsedTypes()).containsExactly(MyEnum.class);
	}

	@Test
	public void testGetName() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class).withName("VALUE1");

		assertThat(value.getName()).isEqualTo("VALUE1");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);

		assertThat(value.accept(new TestValueVisitor())).isEqualTo("ImmutableType:SerializedEnum");
	}

	@Test
	public void testSetName() throws Exception {
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
		SerializedEnum value = new SerializedEnum(MyEnum.class);
		value.setName("VALUE1");

		assertThat(value.toString()).isEqualTo("VALUE1");
	}

	private enum MyEnum {
		VALUE1, VALUE2;
	}

}
