package net.amygdalum.testrecorder.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.util.GenericObject;

@SuppressWarnings("unused")
public class GenericObjectTest {

	@Test
	public void testAsSimple() throws Exception {
		assertThat(new GenericObject() {
			public String str = "myStr";
		}.as(Simple.class).getStr(), equalTo("myStr"));
	}

	@Test
	public void testAsComplex() throws Exception {
		assertThat(new GenericObject() {
			public Simple simple = new GenericObject() {
				public String str = "nestedStr";
			}.as(Simple.class);
		}.as(Complex.class).getSimple().getStr(), equalTo("nestedStr"));
	}

	@Test
	public void testAsSimplePrivateConstructor() throws Exception {
		assertThat(new GenericObject() {
			public String str = "myStr";
		}.as(SimplePrivateConstructor.class).getStr(), equalTo("myStr"));
	}

	@Test
	public void testAsSimpleImplicitConstructor() throws Exception {
		assertThat(new GenericObject() {
			public String str = "myStr";
		}.as(SimpleImplicitConstructor.class).getStr(), equalTo("myStr"));
	}

	@Test
	public void testAsSimpleNoDefaultConstructor() throws Exception {
		assertThat(new GenericObject() {
			public String str = "myStr";
		}.as(SimpleNoDefaultConstructor.class).getStr(), equalTo("myStr"));
	}

	private static class Simple {
		private String str;

		public Simple() {
		}

		public Simple(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

	private static class Complex {

		private Simple simple;

		public Complex() {
			this.simple = new Simple("otherStr");
		}

		public Simple getSimple() {
			return simple;
		}
	}

	private static class SimplePrivateConstructor {
		private String str;

		private SimplePrivateConstructor() {
		}

		public String getStr() {
			return str;
		}
	}

	private static class SimpleImplicitConstructor {
		private String str;

		public String getStr() {
			return str;
		}
	}

	private static class SimpleNoDefaultConstructor {
		private String str;

		public SimpleNoDefaultConstructor(String str) {
			this.str = str;
		}

		public String getStr() {
			return str;
		}
	}

}
