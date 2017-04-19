package net.amygdalum.testrecorder.serializers;

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
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedMap;

@RunWith(MockitoJUnitRunner.class)
public class CollectionsMapSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedMap> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new CollectionsMapSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), containsInAnyOrder(
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
			innerType(Collections.class, "CheckedNavigableMap")));
	}

	@Test
	public void testGenerate() throws Exception {
		Type unmodifiableMapOfString = parameterized(innerType(Collections.class, "UnmodifiableMap"), null, String.class, Integer.class);
		Type setOfString = parameterized(Map.class, null, String.class);

		SerializedMap value = serializer.generate(setOfString, unmodifiableMapOfString);

		assertThat(value.getResultType(), equalTo(setOfString));
		assertThat(value.getType(), equalTo(unmodifiableMapOfString));
		assertThat(value.getMapKeyType(), equalTo(String.class));
		assertThat(value.getMapValueType(), equalTo(Integer.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(Integer.class, 47)).thenReturn(literal(47));
		Type unmodifiableMapOfString = parameterized(innerType(Collections.class, "UnmodifiableMap"), null, String.class, Integer.class);
		Type setOfString = parameterized(Map.class, null, String.class);
		SerializedMap value = serializer.generate(setOfString, unmodifiableMapOfString);

		serializer.populate(value, Collections.singletonMap("Foo", 47));

		assertThat(value.keySet(), containsInAnyOrder(foo));
		assertThat(value.values(), containsInAnyOrder(literal(47)));
	}

}
