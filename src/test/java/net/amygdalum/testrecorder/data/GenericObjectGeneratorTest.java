package net.amygdalum.testrecorder.data;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleExceptionConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimpleExceptionStandardConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimpleNoDefaultConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimpleOnlyExceptionConstructor;
import net.amygdalum.testrecorder.util.testobjects.SimplePrivateConstructor;

public class GenericObjectGeneratorTest {

	@Test
	public void testCreateSimpleClass() throws Exception {
		Simple object = new GenericObjectGenerator<>(Simple.class).create(new TestDataGenerator());

		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateNoDefaultConstructorClass() throws Exception {
		SimpleNoDefaultConstructor object = new GenericObjectGenerator<>(SimpleNoDefaultConstructor.class).create(new TestDataGenerator());

		assertThat(object, notNullValue());
	}

	@Test
	public void testCreatePrivateConstructorClass() throws Exception {
		SimplePrivateConstructor object = new GenericObjectGenerator<>(SimplePrivateConstructor.class).create(new TestDataGenerator());
		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateExceptionConstructorClass() throws Exception {
		SimpleExceptionConstructor object = new GenericObjectGenerator<>(SimpleExceptionConstructor.class).create(new TestDataGenerator());
		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateExceptionStandardConstructorClass() throws Exception {
		SimpleExceptionStandardConstructor object = new GenericObjectGenerator<>(SimpleExceptionStandardConstructor.class).create(new TestDataGenerator());
		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateOnlyExceptionConstructorClass() throws Exception {
		SimpleOnlyExceptionConstructor object = new GenericObjectGenerator<>(SimpleOnlyExceptionConstructor.class).create(new TestDataGenerator());
		assertThat(object, nullValue());
	}
}
