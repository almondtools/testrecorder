package net.amygdalum.testrecorder.values;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedEnumTest {

	@Test
	public void testGetType() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);

		assertThat(value.getResultType(), equalTo(MyEnum.class));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);

		assertThat(value.accept(new TestValueVisitor()), equalTo("SerializedEnum"));
	}

	@Test
	public void testSetGetName() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);
		
		value.setName("VALUE2");
		
		assertThat(value.getName(), equalTo("VALUE2"));
	}

	@Test
	public void testReferencedValues() throws Exception {
		SerializedEnum value = new SerializedEnum(MyEnum.class);
		
		value.setName("VALUE2");

		assertThat(value.referencedValues(), empty());
	}

	@Test
	public void testToString() throws Exception {
		SerializedEnum value= new SerializedEnum(MyEnum.class);
		value.setName("VALUE1");

		assertThat(value.toString(), equalTo("VALUE1"));
	}


	private static enum MyEnum {
		VALUE1, VALUE2;
	}

}
