package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedArray;

@RunWith(MockitoJUnitRunner.class)
public class ArraySerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedArray> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new ArraySerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), empty());
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedArray value = serializer.generate(String[].class, String[].class);

		assertThat(value.getResultType(), equalTo(String[].class));
		assertThat(value.getType(), equalTo(String[].class));
		assertThat(value.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		SerializedArray value = serializer.generate(String[].class, String[].class);

		serializer.populate(value, new String[] { "Foo", "Bar" });

		assertThat(value.getArray(), arrayContaining(foo, bar));
	}

}
