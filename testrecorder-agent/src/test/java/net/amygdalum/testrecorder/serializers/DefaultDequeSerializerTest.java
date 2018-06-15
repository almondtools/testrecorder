package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultDequeSerializerTest {

	private SerializerFacade facade;
	private SerializerSession session;
	private Serializer<SerializedList> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		session = mock(SerializerSession.class);
		serializer = new DefaultDequeSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(ArrayDeque.class, ConcurrentLinkedDeque.class, LinkedBlockingDeque.class);
	}

	@Test
	public void testGenerate() throws Exception {
		Type arrayDequeOfString = parameterized(ArrayDeque.class, null, String.class);

		SerializedList value = serializer.generate(ArrayDeque.class, session);
		value.useAs(arrayDequeOfString);

		assertThat(value.getUsedTypes()).containsExactly(arrayDequeOfString);
		assertThat(value.getType()).isEqualTo(ArrayDeque.class);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo", session)).thenReturn(foo);
		when(facade.serialize(String.class, "Bar", session)).thenReturn(bar);

		SerializedList value = serializer.generate(LinkedBlockingDeque.class, session);
		value.useAs(parameterized(LinkedBlockingDeque.class, null, String.class));

		serializer.populate(value, new LinkedBlockingDeque<>(asList("Foo", "Bar")), session);

		assertThat(value).containsExactly(foo, bar);
	}

}
