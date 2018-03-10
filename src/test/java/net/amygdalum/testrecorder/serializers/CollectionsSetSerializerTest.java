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
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedSet> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new CollectionsSetSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
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
	public void testGenerate() throws Exception {
		Type unmodifiableSetOfString = parameterized(innerType(Collections.class, "UnmodifiableSet"), null, String.class);
		Type setOfString = parameterized(Set.class, null, String.class);

		SerializedSet value = serializer.generate(unmodifiableSetOfString);
		value.useAs(setOfString);

		assertThat(value.getUsedTypes()).containsExactly(setOfString);
		assertThat(value.getType()).isEqualTo(unmodifiableSetOfString);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type unmodifiableSetOfString = parameterized(innerType(Collections.class, "UnmodifiableSet"), null, String.class);
		Type setOfString = parameterized(Set.class, null, String.class);
		SerializedSet value = serializer.generate(setOfString);
		value.useAs(unmodifiableSetOfString);

		serializer.populate(value, new HashSet<>(asList("Foo", "Bar")));

		assertThat(value).containsExactlyInAnyOrder(foo, bar);
	}

	@Test
	public void testPopulateNull() throws Exception {
		SerializedValue foo = literal("Foo");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, null)).thenReturn(nullInstance(String.class));
		Type unmodifiableSetOfString = parameterized(innerType(Collections.class, "UnmodifiableSet"), null, String.class);
		Type setOfString = parameterized(Set.class, null, String.class);
		SerializedSet value = serializer.generate(setOfString);
		value.useAs(unmodifiableSetOfString);

		serializer.populate(value, new HashSet<>(asList("Foo", null)));

		assertThat(value).containsExactlyInAnyOrder(foo, nullInstance(String.class));
	}

}
