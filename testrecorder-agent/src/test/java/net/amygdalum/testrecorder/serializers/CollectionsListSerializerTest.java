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
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListSerializerTest {

	private SerializerFacade facade;
	private SerializerSession session;
	private Serializer<SerializedList> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		session = mock(SerializerSession.class);
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
		Class<?> unmodifiableList = innerType(Collections.class, "UnmodifiableList");
		Type listOfString = parameterized(List.class, null, String.class);

		SerializedList value = serializer.generate(unmodifiableList, session);
		value.useAs(listOfString);

		assertThat(value.getUsedTypes()).containsExactly(listOfString);
		assertThat(value.getType()).isEqualTo(unmodifiableList);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo", session)).thenReturn(foo);
		when(facade.serialize(String.class, "Bar", session)).thenReturn(bar);
		Class<?> unmodifiableList = innerType(Collections.class, "UnmodifiableList");
		SerializedList value = serializer.generate(unmodifiableList, session);

		serializer.populate(value, asList("Foo", "Bar"), session);

		assertThat(value).containsExactly(foo, bar);
	}

	@Test
	public void testPopulateNull() throws Exception {
		SerializedValue foo = literal("Foo");
		when(facade.serialize(String.class, "Foo", session)).thenReturn(foo);
		when(facade.serialize(String.class, null, session)).thenReturn(nullInstance(String.class));
		Class<?> unmodifiableList = innerType(Collections.class, "UnmodifiableList");
		SerializedList value = serializer.generate(unmodifiableList, session);
		value.useAs(parameterized(List.class, null, String.class));

		serializer.populate(value, asList("Foo", null), session);

		assertThat(value).containsExactly(foo, nullInstance(String.class));
	}

}
