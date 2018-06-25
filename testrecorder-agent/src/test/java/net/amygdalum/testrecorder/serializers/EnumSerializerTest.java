package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class EnumSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedEnum> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new EnumSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).isEmpty();
	}

	@Test
	void testGenerate() throws Exception {
		SerializedEnum value = serializer.generate(MyEnum.class, session);
		value.useAs(MyInterface.class);

		assertThat(value.getUsedTypes()).containsExactly(MyInterface.class);
		assertThat(value.getType()).isEqualTo(MyEnum.class);
	}

	@Test
	void testGenerateWithExtendedEnum() throws Exception {
		SerializedEnum value = serializer.generate(ExtendedEnum.VALUE1.getClass(), session);
		value.useAs(MyInterface.class);

		assertThat(value.getUsedTypes()).containsExactly(MyInterface.class);
		assertThat(value.getType()).isEqualTo(ExtendedEnum.class);
	}

	@Test
	void testPopulate() throws Exception {
		SerializedEnum value = serializer.generate(MyEnum.class, session);
		value.useAs(MyInterface.class);

		serializer.populate(value, MyEnum.VALUE1, session);

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
