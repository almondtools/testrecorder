package net.amygdalum.testrecorder.serializers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedImmutable;


public class ClassSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedImmutable<Class<?>>> serializer;

	@BeforeEach
	public void before() throws Exception {
		facade = mock(SerializerFacade.class);
		serializer = new ClassSerializer.Factory().newSerializer(facade);
	}

	@Test
	public void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses(), contains(Class.class));
	}

	@Test
	public void testGenerate() throws Exception {
		SerializedImmutable<Class<?>> value = serializer.generate(Class.class, Class.class);

		assertThat(value.getResultType()).isEqualTo(Class.class);
		assertThat(value.getType()).isEqualTo(Class.class);
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedImmutable<Class<?>> value = serializer.generate(Class.class, Class.class);

		serializer.populate(value, String.class);

		assertThat(value.getValue()).isSameAs(String.class);
	}

}
