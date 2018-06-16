package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedList> serializer;

	@BeforeEach
	public void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new DefaultListSerializer();
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(LinkedList.class, ArrayList.class, Vector.class);
	}

	@Test
	public void testGenerate() throws Exception {
		Type linkedListOfString = parameterized(LinkedList.class, null, String.class);

		SerializedList value = serializer.generate(LinkedList.class, session);
		value.useAs(linkedListOfString);

		assertThat(value.getUsedTypes()).containsExactly(linkedListOfString);
		assertThat(value.getType()).isEqualTo(LinkedList.class);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(session.find("Foo")).thenReturn(foo);
		when(session.find("Bar")).thenReturn(bar);

		SerializedList value = serializer.generate(LinkedList.class, session);
		value.useAs(parameterized(LinkedList.class, null, String.class));

		serializer.populate(value, asList("Foo", "Bar"), session);

		assertThat(value).containsExactly(foo, bar);
	}

}
