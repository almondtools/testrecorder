package net.amygdalum.testrecorder.serializers;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedEnum;

@RunWith(MockitoJUnitRunner.class)
public class EnumSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedEnum> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new EnumSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), empty());
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedEnum value = serializer.generate(MyInterface.class, MyEnum.class);

		assertThat(value.getResultType(), equalTo(MyInterface.class));
		assertThat(value.getType(), equalTo(MyEnum.class));
	}

	@Test
	public void testGenerateWithExtendedEnum() throws Exception {
		SerializedEnum value = serializer.generate(MyInterface.class, ExtendedEnum.VALUE1.getClass());

		assertThat(value.getResultType(), equalTo(MyInterface.class));
		assertThat(value.getType(), equalTo(ExtendedEnum.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedEnum value = serializer.generate(MyInterface.class, MyEnum.class);

		serializer.populate(value, MyEnum.VALUE1);

		assertThat(value.getName(), equalTo("VALUE1"));
	}

	interface MyInterface {
		
	}
	
	private static enum MyEnum implements MyInterface {
		VALUE1, VALUE2;
	}

	private static enum ExtendedEnum {
		VALUE1 {};
	}
}
