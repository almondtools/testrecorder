package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedList> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new CollectionsListSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(
			innerType(Collections.class, "UnmodifiableList"),
			innerType(Collections.class, "UnmodifiableRandomAccessList"),
			innerType(Collections.class, "SynchronizedList"),
			innerType(Collections.class, "SynchronizedRandomAccessList"),
			innerType(Collections.class, "EmptyList"),
			innerType(Collections.class, "SingletonList"),
			innerType(Collections.class, "CheckedList"),
			innerType(Collections.class, "CheckedRandomAccessList"));
	}

	@Test
	public void testGenerate() throws Exception {
		Type unmodifiableListOfString = parameterized(innerType(Collections.class, "UnmodifiableList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);

		SerializedList value = serializer.generate(unmodifiableListOfString);
		value.useAs(listOfString);

		assertThat(value.getUsedTypes()).containsExactly(listOfString);
		assertThat(value.getType()).isEqualTo(unmodifiableListOfString);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type unmodifiableListOfString = parameterized(innerType(Collections.class, "UnmodifiableList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);
		SerializedList value = serializer.generate(listOfString);
		value.useAs(unmodifiableListOfString);

		serializer.populate(value, asList("Foo", "Bar"));

		assertThat(value).containsExactly(foo, bar);
	}

	@Test
	public void testPopulateNull() throws Exception {
		SerializedValue foo = literal("Foo");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, null)).thenReturn(nullInstance(String.class));
		Type unmodifiableListOfString = parameterized(innerType(Collections.class, "UnmodifiableList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);
		SerializedList value = serializer.generate(listOfString);
		value.useAs(unmodifiableListOfString);

		serializer.populate(value, asList("Foo", null));

		assertThat(value).containsExactly(foo, nullInstance(String.class));
	}

}
