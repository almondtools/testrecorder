package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.SerializedValues.nullValue;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedMap> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new CollectionsMapSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(
			innerType(Collections.class, "UnmodifiableMap"),
			innerType(Collections.class, "UnmodifiableSortedMap"),
			innerType(Collections.class, "UnmodifiableNavigableMap"),
			innerType(Collections.class, "SynchronizedMap"),
			innerType(Collections.class, "SynchronizedSortedMap"),
			innerType(Collections.class, "SynchronizedNavigableMap"),
			innerType(Collections.class, "EmptyMap"),
			innerType(Collections.class, "SingletonMap"),
			innerType(Collections.class, "CheckedMap"),
			innerType(Collections.class, "CheckedSortedMap"),
			innerType(Collections.class, "CheckedNavigableMap"));
	}

	@Test
	void testGenerate() throws Exception {
		Class<?> unmodifiableMap = innerType(Collections.class, "UnmodifiableMap");

		SerializedMap value = serializer.generate(unmodifiableMap, session);
		value.useAs(parameterized(Map.class, null, String.class, Integer.class));

		assertThat(value.getUsedTypes()).containsExactly(parameterized(Map.class, null, String.class, Integer.class));
		assertThat(value.getType()).isEqualTo(unmodifiableMap);
		assertThat(value.getMapKeyType()).isEqualTo(String.class);
		assertThat(value.getMapValueType()).isEqualTo(Integer.class);
	}

	@Nested
	class testPopulate {

		@Test
		void onCommon() throws Exception {
			SerializedValue foo = literal("Foo");
			when(session.find("Foo")).thenReturn(foo);
			when(session.find(47)).thenReturn(literal(47));
			Class<?> unmodifiableMap = innerType(Collections.class, "UnmodifiableMap");
			SerializedMap value = serializer.generate(unmodifiableMap, session);
			value.useAs(parameterized(Map.class, null, String.class, Integer.class));

			serializer.populate(value, Collections.singletonMap("Foo", 47), session);

			assertThat(value.keySet()).containsExactlyInAnyOrder(foo);
			assertThat(value.values()).containsExactlyInAnyOrder(literal(47));
		}

		@Test
		void onNullKey() throws Exception {
			when(session.find(47)).thenReturn(literal(47));
			Class<?> unmodifiableMap = innerType(Collections.class, "UnmodifiableMap");
			Type mapOfStringInteger = parameterized(Map.class, null, String.class, Integer.class);
			SerializedMap value = serializer.generate(unmodifiableMap, session);
			value.useAs(mapOfStringInteger);

			serializer.populate(value, Collections.singletonMap(null, 47), session);

			assertThat(value.keySet()).containsExactlyInAnyOrder(nullValue(String.class));
			assertThat(value.values()).containsExactlyInAnyOrder(literal(47));
		}

		@Test
		void onNullValue() throws Exception {
			SerializedValue foo = literal("Foo");
			when(session.find("Foo")).thenReturn(foo);
			Class<?> unmodifiableMap = innerType(Collections.class, "UnmodifiableMap");
			Type mapOfStringInteger = parameterized(Map.class, null, String.class, Integer.class);
			SerializedMap value = serializer.generate(unmodifiableMap, session);
			value.useAs(mapOfStringInteger);

			serializer.populate(value, Collections.singletonMap("Foo", null), session);

			assertThat(value.keySet()).containsExactlyInAnyOrder(foo);
			assertThat(value.values()).containsExactlyInAnyOrder(nullValue(Integer.class));
		}
	}
}
