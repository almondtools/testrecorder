package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.innerType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.Arrays;
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
public class ArraysListSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedList> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new ArraysListSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), contains(innerType(Arrays.class, "ArrayList")));
	}

	@Test
	public void testGenerate() throws Exception {
		Type arrayListOfString = parameterized(innerType(Arrays.class, "ArrayList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);

		SerializedList value = serializer.generate(listOfString, arrayListOfString);

		assertThat(value.getResultType(), equalTo(listOfString));
		assertThat(value.getType(), equalTo(arrayListOfString));
		assertThat(value.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type arrayListOfString = parameterized(innerType(Arrays.class, "ArrayList"), null, String.class);
		Type listOfString = parameterized(List.class, null, String.class);
		SerializedList value = serializer.generate(listOfString, arrayListOfString);

		serializer.populate(value, asList("Foo", "Bar"));

		assertThat(value, contains(foo, bar));
	}

}
