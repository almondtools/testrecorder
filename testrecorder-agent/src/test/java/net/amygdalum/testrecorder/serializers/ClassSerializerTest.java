package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class ClassSerializerTest {

	private SerializerFacade facade;
	private SerializerSession session;
	private Serializer<SerializedImmutable<Class<?>>> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		session = mock(SerializerSession.class);
		serializer = new ClassSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).containsExactly(Class.class);
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedImmutable<Class<?>> value = serializer.generate(Class.class, session);
		value.useAs(Class.class);

		assertThat(value.getUsedTypes()).containsExactly(Class.class);
		assertThat(value.getType()).isEqualTo(Class.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedImmutable<Class<?>> value = serializer.generate(Class.class, session);
		value.useAs(Class.class);

		serializer.populate(value, String.class, session);

		assertThat(value.getValue()).isSameAs(String.class);
	}

}
