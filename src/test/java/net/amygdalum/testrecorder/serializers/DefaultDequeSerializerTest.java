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
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedList;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDequeSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedList> serializer;

	@Before
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new DefaultDequeSerializer.Factory().newSerializer(facade);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), containsInAnyOrder(ArrayDeque.class, ConcurrentLinkedDeque.class, LinkedBlockingDeque.class));
	}

	@Test
	public void testGenerate() throws Exception {
		Type arrayDequeOfString = parameterized(ArrayDeque.class, null, String.class);

		SerializedList value = serializer.generate(arrayDequeOfString, ArrayDeque.class);

		assertThat(value.getResultType(), equalTo(arrayDequeOfString));
		assertThat(value.getType(), equalTo(ArrayDeque.class));
		assertThat(value.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type linkedBlockingDequeOfString = parameterized(LinkedBlockingDeque.class, null, String.class);
		SerializedList value = serializer.generate(linkedBlockingDequeOfString, LinkedBlockingDeque.class);

		serializer.populate(value, new LinkedBlockingDeque<>(asList("Foo", "Bar")));

		assertThat(value, contains(foo, bar));
	}

}
