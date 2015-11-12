package com.almondtools.testrecorder;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.junit.Test;

@SuppressWarnings("unused")
public class GenericMatcherTest {

	@Test
	public void testMatchesSimple() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.matches(new Simple("myStr")), is(true));
	}

	@Test
	public void testMatcherSimple() throws Exception {
		assertThat(new Simple("myStr"), new GenericMatcher() {
			public String str = "myStr";

		}.matcher(Simple.class));
	}

	@Test
	public void testMatchesComplex() throws Exception {
		assertThat(new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matcher(Simple.class);
		}.matches(new Complex()), is(true));
	}

	@Test
	public void testMatcherComplex() throws Exception {
		assertThat(new Complex(), new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matcher(Simple.class);
		}.matcher(Complex.class));
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
