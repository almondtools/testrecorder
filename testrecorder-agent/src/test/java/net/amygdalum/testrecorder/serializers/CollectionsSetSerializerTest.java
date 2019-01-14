package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.SerializedValues.nullValue;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedSet> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new CollectionsSetSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(
			innerType(Collections.class, "UnmodifiableSet"),
			innerType(Collections.class, "UnmodifiableSortedSet"),
			innerType(Collections.class, "UnmodifiableNavigableSet"),
			innerType(Collections.class, "SynchronizedSet"),
			innerType(Collections.class, "SynchronizedSortedSet"),
			innerType(Collections.class, "SynchronizedNavigableSet"),
			innerType(Collections.class, "EmptySet"),
			innerType(Collections.class, "SingletonSet"),
			innerType(Collections.class, "CheckedSet"),
			innerType(Collections.class, "CheckedSortedSet"),
			innerType(Collections.class, "CheckedNavigableSet"));
	}

	@Test
	void testGenerate() throws Exception {
		Class<?> unmodifiableSet = innerType(Collections.class, "UnmodifiableSet");

		SerializedSet value = serializer.generate(unmodifiableSet, session);
		value.useAs(parameterized(Set.class, null, String.class));

		assertThat(value.getUsedTypes()).containsExactly(parameterized(Set.class, null, String.class));
		assertThat(value.getType()).isEqualTo(unmodifiableSet);
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
			Class<?> unmodifiableSet = innerType(Collections.class, "UnmodifiableSet");
			SerializedSet value = serializer.generate(unmodifiableSet, session);
			value.useAs(parameterized(Set.class, null, String.class));

			serializer.populate(value, new HashSet<>(asList("Foo", "Bar")), session);

			assertThat(value).containsExactlyInAnyOrder(foo, bar);
		}

		@Test
		void onNull() throws Exception {
			SerializedValue foo = literal("Foo");
			when(session.find("Foo")).thenReturn(foo);
			Class<?> unmodifiableSet = innerType(Collections.class, "UnmodifiableSet");
			SerializedSet value = serializer.generate(unmodifiableSet, session);
			value.useAs(parameterized(Set.class, null, String.class));

			serializer.populate(value, new HashSet<>(asList("Foo", null)), session);

			assertThat(value).containsExactlyInAnyOrder(foo, nullValue(String.class));
		}
	}
}
