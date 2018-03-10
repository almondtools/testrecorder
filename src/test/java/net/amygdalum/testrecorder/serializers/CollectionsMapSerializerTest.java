package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedMap> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new CollectionsMapSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
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
	public void testGenerate() throws Exception {
		Type unmodifiableMapOfString = parameterized(innerType(Collections.class, "UnmodifiableMap"), null, String.class, Integer.class);
		Type setOfString = parameterized(Map.class, null, String.class);

		SerializedMap value = serializer.generate(unmodifiableMapOfString);
		value.useAs(setOfString);

		assertThat(value.getUsedTypes()).containsExactly(setOfString);
		assertThat(value.getType()).isEqualTo(unmodifiableMapOfString);
		assertThat(value.getMapKeyType()).isEqualTo(String.class);
		assertThat(value.getMapValueType()).isEqualTo(Integer.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(Integer.class, 47)).thenReturn(literal(47));
		Type unmodifiableMapOfString = parameterized(innerType(Collections.class, "UnmodifiableMap"), null, String.class, Integer.class);
		Type mapOfStringInteger = parameterized(Map.class, null, String.class, Integer.class);
		SerializedMap value = serializer.generate(mapOfStringInteger);
		value.useAs(unmodifiableMapOfString);

		serializer.populate(value, Collections.singletonMap("Foo", 47));

		assertThat(value.keySet()).containsExactlyInAnyOrder(foo);
		assertThat(value.values()).containsExactlyInAnyOrder(literal(47));
	}

	@Test
	public void testPopulateWithNullKey() throws Exception {
		when(facade.serialize(String.class, null)).thenReturn(nullInstance(String.class));
		when(facade.serialize(Integer.class, 47)).thenReturn(literal(47));
		Type unmodifiableMapOfString = parameterized(innerType(Collections.class, "UnmodifiableMap"), null, String.class, Integer.class);
		Type mapOfStringInteger = parameterized(Map.class, null, String.class, Integer.class);
		SerializedMap value = serializer.generate(mapOfStringInteger);
		value.useAs(unmodifiableMapOfString);

		serializer.populate(value, Collections.singletonMap(null, 47));

		assertThat(value.keySet()).containsExactlyInAnyOrder(nullInstance(String.class));
		assertThat(value.values()).containsExactlyInAnyOrder(literal(47));
	}

	@Test
	public void testPopulateWithNullValue() throws Exception {
		SerializedValue foo = literal("Foo");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(Integer.class, null)).thenReturn(nullInstance(Integer.class));
		Type unmodifiableMapOfString = parameterized(innerType(Collections.class, "UnmodifiableMap"), null, String.class, Integer.class);
		Type mapOfStringInteger = parameterized(Map.class, null, String.class, Integer.class);
		SerializedMap value = serializer.generate(mapOfStringInteger);
		value.useAs(unmodifiableMapOfString);

		serializer.populate(value, Collections.singletonMap("Foo", null));

		assertThat(value.keySet()).containsExactlyInAnyOrder(foo);
		assertThat(value.values()).containsExactlyInAnyOrder(nullInstance(Integer.class));
	}

}
