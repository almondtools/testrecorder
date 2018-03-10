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
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedSet> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new DefaultSetSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactlyInAnyOrder(HashSet.class, TreeSet.class, LinkedHashSet.class);
	}

	@Test
	public void testGenerate() throws Exception {
		Type hashSetOfString = parameterized(HashSet.class, null, String.class);

		SerializedSet value = serializer.generate(HashSet.class);
		value.useAs(hashSetOfString);

		assertThat(value.getUsedTypes()).containsExactly(hashSetOfString);
		assertThat(value.getType()).isEqualTo(HashSet.class);
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedValue foo = literal("Foo");
		SerializedValue bar = literal("Bar");
		when(facade.serialize(String.class, "Foo")).thenReturn(foo);
		when(facade.serialize(String.class, "Bar")).thenReturn(bar);
		Type hashSetOfString = parameterized(HashSet.class, null, String.class);
		SerializedSet value = serializer.generate(hashSetOfString);
		value.useAs(HashSet.class);

		serializer.populate(value, new HashSet<>(asList("Foo", "Bar")));

		assertThat(value).containsExactlyInAnyOrder(foo, bar);
	}

}
