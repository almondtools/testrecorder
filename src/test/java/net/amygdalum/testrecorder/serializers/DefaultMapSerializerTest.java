package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.singletonMap;
import static net.amygdalum.testrecorder.deserializers.TypeManager.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedMap;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMapSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedMap> serializer;
	
	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new DefaultMapSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), containsInAnyOrder(HashMap.class, TreeMap.class, LinkedHashMap.class));
	}

	@Test
	public void testGenerate() throws Exception {
		Type hashMapOfStringInteger = parameterized(HashMap.class, null, String.class, Integer.class);
		
		SerializedMap value = serializer.generate(hashMapOfStringInteger, HashMap.class);
		
		assertThat(value.getType(), equalTo(hashMapOfStringInteger));
		assertThat(value.getValueType(), equalTo(HashMap.class));
		assertThat(value.getMapKeyType(), equalTo(String.class));
		assertThat(value.getMapValueType(), equalTo(Integer.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal(String.class, "Foo");
		SerializedValue bar = literal(String.class, "Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type hashMapOfStringInteger = parameterized(HashMap.class, null, String.class, Integer.class);
		SerializedMap value = serializer.generate(hashMapOfStringInteger, HashMap.class);

		serializer.populate(value, singletonMap("Foo", "Bar"));

		assertThat(value, hasEntry(foo, bar));
	}

}
