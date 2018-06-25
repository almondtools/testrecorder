package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetSerializerTest {

	private SerializerSession session;
	private Serializer<SerializedSet> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new DefaultSetSerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(HashSet.class, TreeSet.class, LinkedHashSet.class);
	}

	@Test
	void testGenerate() throws Exception {
		Type hashSetOfString = parameterized(HashSet.class, null, String.class);

		SerializedSet value = serializer.generate(HashSet.class, session);
		value.useAs(hashSetOfString);

		assertThat(value.getUsedTypes()).containsExactly(hashSetOfString);
		assertThat(value.getType()).isEqualTo(HashSet.class);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	void testComponents() throws Exception {
		assertThat(serializer.components(new HashSet<>(asList("Foo", "Bar")), session).map(o -> (Object) o))
			.contains(new Object[] { "Foo", "Bar" });
	}

	@Test
	void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(session.find("Foo")).thenReturn(foo);
		when(session.find("Bar")).thenReturn(bar);

		SerializedSet value = serializer.generate(HashSet.class, session);
		value.useAs(parameterized(HashSet.class, null, String.class));

		serializer.populate(value, new HashSet<>(asList("Foo", "Bar")), session);

		assertThat(value).containsExactlyInAnyOrder(foo, bar);
	}

}
