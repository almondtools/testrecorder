package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.SerializedValues.nullValue;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedList> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new CollectionsListSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
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
	void testGenerate() throws Exception {
		Class<?> unmodifiableList = innerType(Collections.class, "UnmodifiableList");
		Type listOfString = parameterized(List.class, null, String.class);

		SerializedList value = serializer.generate(unmodifiableList, session);
		value.useAs(listOfString);

		assertThat(value.getUsedTypes()).containsExactly(listOfString);
		assertThat(value.getType()).isEqualTo(unmodifiableList);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Nested
	class testPopulate {
		@Test
		void onCommon() throws Exception {
			SerializedValue foo = literal("Foo");
			SerializedValue bar = literal("Bar");
			when(session.find("Foo")).thenReturn(foo);
			when(session.find("Bar")).thenReturn(bar);
			Class<?> unmodifiableList = innerType(Collections.class, "UnmodifiableList");
			SerializedList value = serializer.generate(unmodifiableList, session);

			serializer.populate(value, asList("Foo", "Bar"), session);

			assertThat(value).containsExactly(foo, bar);
		}

		@Test
		void onNull() throws Exception {
			SerializedValue foo = literal("Foo");
			when(session.find("Foo")).thenReturn(foo);
			Class<?> unmodifiableList = innerType(Collections.class, "UnmodifiableList");
			SerializedList value = serializer.generate(unmodifiableList, session);
			value.useAs(parameterized(List.class, null, String.class));

			serializer.populate(value, asList("Foo", null), session);

			assertThat(value).containsExactly(foo, nullValue(String.class));
		}
	}

}
