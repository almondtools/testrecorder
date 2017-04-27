package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedList;

@RunWith(MockitoJUnitRunner.class)
public class CollectionsListSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedList> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new CollectionsListSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), containsInAnyOrder(
			innerType(Collections.class, "UnmodifiableList"),
			innerType(Collections.class, "UnmodifiableRandomAccessList"),
			innerType(Collections.class, "SynchronizedList"),
			innerType(Collections.class, "SynchronizedRandomAccessList"),
			innerType(Collections.class, "EmptyList"),
			innerType(Collections.class, "SingletonList"),
			innerType(Collections.class, "CheckedList"),
			innerType(Collections.class, "CheckedRandomAccessList")));
	}

	@Test
	public void testGenerate() throws Exception {
		Type unmodifiableListOfString = parameterized(innerType(Collections.class, "UnmodifiableList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);

		SerializedList value = serializer.generate(listOfString, unmodifiableListOfString);

		assertThat(value.getResultType(), equalTo(listOfString));
		assertThat(value.getType(), equalTo(unmodifiableListOfString));
		assertThat(value.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type unmodifiableListOfString = parameterized(innerType(Collections.class, "UnmodifiableList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);
		SerializedList value = serializer.generate(listOfString, unmodifiableListOfString);

		serializer.populate(value, asList("Foo", "Bar"));

		assertThat(value, contains(foo, bar));
	}

	@Test
	public void testPopulateNull() throws Exception {
	    SerializedValue foo = literal("Foo");
	    when(facade.serialize(String.class, "Foo")).thenReturn(foo);
	    when(facade.serialize(String.class, null)).thenReturn(nullInstance(String.class));
	    Type unmodifiableListOfString = parameterized(innerType(Collections.class, "UnmodifiableList"), null, String.class);
	    Type listOfString = parameterized(List.class, null, String.class);
	    SerializedList value = serializer.generate(listOfString, unmodifiableListOfString);
	    
	    serializer.populate(value, asList("Foo", null));
	    
	    assertThat(value, contains(foo, nullInstance(String.class)));
	}
	
}
