package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.singletonMap;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapSerializerTest {

	private SerializerFacade facade;
	private SerializerSession session;
	private Serializer<SerializedMap> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		session = mock(SerializerSession.class);
		serializer = new DefaultMapSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(HashMap.class, TreeMap.class, LinkedHashMap.class);
	}

	@Test
	public void testGenerate() throws Exception {
		Type hashMapOfStringInteger = parameterized(HashMap.class, null, String.class, Integer.class);

		SerializedMap value = serializer.generate(HashMap.class, session);
		value.useAs(hashMapOfStringInteger);

		assertThat(value.getUsedTypes()).containsExactly(hashMapOfStringInteger);
		assertThat(value.getType()).isEqualTo(HashMap.class);
		assertThat(value.getMapKeyType()).isEqualTo(String.class);
		assertThat(value.getMapValueType()).isEqualTo(Integer.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue i42 = literal(Integer.class, 42);
		when(facade.serialize(String.class, "Foo", session)).thenReturn(foo);
		when(facade.serialize(Integer.class, 42, session)).thenReturn(i42);

		SerializedMap value = serializer.generate(HashMap.class, session);
		value.useAs(parameterized(HashMap.class, null, String.class, Integer.class));

		serializer.populate(value, singletonMap("Foo", 42), session);

		assertThat(value).containsExactly(entry(foo, i42));
	}

}
