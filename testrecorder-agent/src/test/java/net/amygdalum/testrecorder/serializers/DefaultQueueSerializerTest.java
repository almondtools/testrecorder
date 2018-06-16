package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultQueueSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedList> serializer;

	@BeforeEach
	public void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new DefaultQueueSerializer();
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(LinkedBlockingQueue.class, ArrayBlockingQueue.class, ConcurrentLinkedQueue.class, PriorityBlockingQueue.class,
			LinkedTransferQueue.class, DelayQueue.class);
	}

	@Test
	public void testGenerate() throws Exception {
		Type priorityBlockingQueueOfString = parameterized(PriorityBlockingQueue.class, null, String.class);

		SerializedList value = serializer.generate(PriorityBlockingQueue.class, session);
		value.useAs(priorityBlockingQueueOfString);

		assertThat(value.getUsedTypes()).containsExactly(priorityBlockingQueueOfString);
		assertThat(value.getType()).isEqualTo(PriorityBlockingQueue.class);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(session.find("Foo")).thenReturn(foo);
		when(session.find("Bar")).thenReturn(bar);

		SerializedList value = serializer.generate(LinkedBlockingQueue.class, session);
		value.useAs(parameterized(LinkedBlockingQueue.class, null, String.class));

		serializer.populate(value, new LinkedBlockingQueue<>(asList("Foo", "Bar")), session);

		assertThat(value).containsExactly(foo, bar);
	}

}
