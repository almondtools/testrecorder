package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedArray;

public class ArraySerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedArray> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new ArraySerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).isEmpty();
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedArray value = serializer.generate(String[].class);
		value.useAs(String[].class);

		assertThat(value.getUsedTypes()).containsExactly(String[].class);
		assertThat(value.getType()).isEqualTo(String[].class);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		SerializedArray value = serializer.generate(String[].class);
		value.useAs(String[].class);

		serializer.populate(value, new String[] { "Foo", "Bar" });

		assertThat(value.getArray()).containsExactly(foo, bar);
	}

}
