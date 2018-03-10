package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedList> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new ArraysListSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactly(innerType(Arrays.class, "ArrayList"));
	}

	@Test
	public void testGenerate() throws Exception {
		Type arrayListOfString = parameterized(innerType(Arrays.class, "ArrayList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);

		SerializedList value = serializer.generate(arrayListOfString);
		value.useAs(listOfString);

		assertThat(value.getUsedTypes()).containsExactly(listOfString);
		assertThat(value.getType()).isEqualTo(arrayListOfString);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type arrayListOfString = parameterized(innerType(Arrays.class, "ArrayList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);
		SerializedList value = serializer.generate(listOfString);
		value.useAs(arrayListOfString);

		serializer.populate(value, asList("Foo", "Bar"));

		assertThat(value).containsExactly(foo, bar);
	}

}
