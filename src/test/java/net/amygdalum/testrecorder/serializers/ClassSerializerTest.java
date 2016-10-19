package net.amygdalum.testrecorder.serializers;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedImmutable;

@RunWith(MockitoJUnitRunner.class)
public class ClassSerializerTest {

	private SerializerFacade facade;
	private Serializer<SerializedImmutable<Class<?>>> serializer;

	@Before
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

		assertThat(value.getResultType(), equalTo(Class.class));
		assertThat(value.getType(), equalTo(Class.class));
	}

	@Test
	public void testPopulate() throws Exception {
		SerializedImmutable<Class<?>> value = serializer.generate(Class.class, Class.class);

		serializer.populate(value, String.class);

		assertThat(value.getValue(), sameInstance(String.class));
	}

}
