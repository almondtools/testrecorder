package net.amygdalum.testrecorder.data;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class GenericObjectGeneratorTest {

	@Test
	public void testCreateSimpleClass() throws Exception {
		Simple object = new GenericObjectGenerator<>(Simple.class).create(new TestDataGenerator());

		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateNoDefaultConstructorClass() throws Exception {
		NoDefaultConstructor object = new GenericObjectGenerator<>(NoDefaultConstructor.class).create(new TestDataGenerator());

		assertThat(object, notNullValue());
	}

	@Test
	public void testCreatePrivateConstructorClass() throws Exception {
		PrivateConstructor object = new GenericObjectGenerator<>(PrivateConstructor.class).create(new TestDataGenerator());
		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateExceptionConstructorClass() throws Exception {
		ExceptionConstructor object = new GenericObjectGenerator<>(ExceptionConstructor.class).create(new TestDataGenerator());
		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateExceptionStandardConstructorClass() throws Exception {
		ExceptionStandardConstructor object = new GenericObjectGenerator<>(ExceptionStandardConstructor.class).create(new TestDataGenerator());
		assertThat(object, notNullValue());
	}

	@Test
	public void testCreateOnlyExceptionConstructorClass() throws Exception {
		OnlyExceptionConstructor object = new GenericObjectGenerator<>(OnlyExceptionConstructor.class).create(new TestDataGenerator());
		assertThat(object, nullValue());
	}

	@SuppressWarnings("unused")
	public static class Simple {
		
		private String s;

		public Simple() {
		}

	}

	public static class PrivateConstructor {

		private PrivateConstructor() {
		}

	}

	@SuppressWarnings("unused")
	public static class NoDefaultConstructor {

		private boolean value;

		public NoDefaultConstructor(boolean value) {
			this.value = value;
		}

	}

	public static class ExceptionStandardConstructor {

		public ExceptionStandardConstructor() {
			throw new RuntimeException();
		}

		public ExceptionStandardConstructor(int value) {
		}

	}

	public static class ExceptionConstructor {

		public ExceptionConstructor(boolean value) {
			throw new RuntimeException();
		}

		public ExceptionConstructor(int value) {
		}
	}

	public static class OnlyExceptionConstructor {

		public OnlyExceptionConstructor() {
			throw new RuntimeException();
		}
	}
}
