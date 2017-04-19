package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedSet;

@RunWith(MockitoJUnitRunner.class)
public class CollectionsSetSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedSet> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new CollectionsSetSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), containsInAnyOrder(
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
			innerType(Collections.class, "CheckedNavigableSet")));
	}

	@Test
	public void testGenerate() throws Exception {
		Type unmodifiableSetOfString = parameterized(innerType(Collections.class, "UnmodifiableSet"), null, String.class);
		Type setOfString = parameterized(Set.class, null, String.class);

		SerializedSet value = serializer.generate(setOfString, unmodifiableSetOfString);

		assertThat(value.getResultType(), equalTo(setOfString));
		assertThat(value.getType(), equalTo(unmodifiableSetOfString));
		assertThat(value.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type unmodifiableSetOfString = parameterized(innerType(Collections.class, "UnmodifiableSet"), null, String.class);
		Type setOfString = parameterized(Set.class, null, String.class);
		SerializedSet value = serializer.generate(setOfString, unmodifiableSetOfString);

		serializer.populate(value, new HashSet<>(asList("Foo", "Bar")));

		assertThat(value, containsInAnyOrder(foo, bar));
	}

}
