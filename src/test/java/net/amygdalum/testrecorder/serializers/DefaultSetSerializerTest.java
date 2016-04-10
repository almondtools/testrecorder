package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedSet;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSetSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedSet> serializer;
	
	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new DefaultSetSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), containsInAnyOrder(HashSet.class, TreeSet.class, LinkedHashSet.class));
	}

	@Test
	public void testGenerate() throws Exception {
		Type hashSetOfString = parameterized(HashSet.class, null, String.class);
		
		SerializedSet value = serializer.generate(hashSetOfString, HashSet.class);
		
		assertThat(value.getResultType(), equalTo(hashSetOfString));
		assertThat(value.getType(), equalTo(HashSet.class));
		assertThat(value.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal(String.class, "Foo");
		SerializedValue bar = literal(String.class, "Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type hashSetOfString = parameterized(HashSet.class, null, String.class);
		SerializedSet value = serializer.generate(hashSetOfString, HashSet.class);

		serializer.populate(value, new HashSet<>(asList("Foo", "Bar")));

		assertThat(value, containsInAnyOrder(foo, bar));
	}

}
