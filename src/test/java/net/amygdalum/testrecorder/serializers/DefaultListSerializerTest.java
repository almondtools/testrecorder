package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedList;

@RunWith(MockitoJUnitRunner.class)
public class DefaultListSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedList> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new DefaultListSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), containsInAnyOrder(LinkedList.class, ArrayList.class, Vector.class));
	}

	@Test
	public void testGenerate() throws Exception {
		Type linkedListOfString = parameterized(LinkedList.class, null, String.class);

		SerializedList value = serializer.generate(linkedListOfString, LinkedList.class);

		assertThat(value.getResultType(), equalTo(linkedListOfString));
		assertThat(value.getType(), equalTo(LinkedList.class));
		assertThat(value.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type linkedListOfString = parameterized(LinkedList.class, null, String.class);
		SerializedList value = serializer.generate(linkedListOfString, LinkedList.class);

		serializer.populate(value, asList("Foo", "Bar"));

		assertThat(value, contains(foo, bar));
	}

}
