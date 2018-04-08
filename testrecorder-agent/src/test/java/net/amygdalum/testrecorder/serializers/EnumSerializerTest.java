package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class EnumSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedEnum> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new EnumSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).isEmpty();
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedEnum value = serializer.generate(MyEnum.class);
		value.useAs(MyInterface.class);

		assertThat(value.getUsedTypes()).containsExactly(MyInterface.class);
		assertThat(value.getType()).isEqualTo(MyEnum.class);
	}

	@Test
	public void testGenerateWithExtendedEnum() throws Exception {
		SerializedEnum value = serializer.generate(ExtendedEnum.VALUE1.getClass());
		value.useAs(MyInterface.class);

		assertThat(value.getUsedTypes()).containsExactly(MyInterface.class);
		assertThat(value.getType()).isEqualTo(ExtendedEnum.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedEnum value = serializer.generate(MyEnum.class);
		value.useAs(MyInterface.class);

		serializer.populate(value, MyEnum.VALUE1);

		assertThat(value.getName()).isEqualTo("VALUE1");
	}

	interface MyInterface {

	}

	private static enum MyEnum implements MyInterface {
		VALUE1, VALUE2;
	}

	private static enum ExtendedEnum {
		VALUE1 {
		};
	}
}
