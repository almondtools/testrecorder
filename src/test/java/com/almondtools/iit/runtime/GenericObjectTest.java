package com.almondtools.iit.runtime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.junit.Test;

@SuppressWarnings("unused")
public class GenericObjectTest {

	@Test
	public void testAsSimple() throws Exception {
		assertThat(new GenericObject() {
			public String str = "myStr";
		}.as(Simple.class).getStr(), equalTo("myStr"));
	}

	@Test
	public void testMatchesSimple() throws Exception {
		assertThat(new GenericObject() {
			public String str = "myStr";

		}.matches(new Simple("myStr")), is(true));
	}

	@Test
	public void testMatcherSimple() throws Exception {
		assertThat(new Simple("myStr"), new GenericObject() {
			public String str = "myStr";

		}.matcher(Simple.class));
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
	public void testMatchesComplex() throws Exception {
		assertThat(new GenericObject() {
			public Matcher<Simple> simple = new GenericObject() {
				public String str = "otherStr";
			}.matcher(Simple.class);
		}.matches(new Complex()), is(true));
	}

	@Test
	public void testMatcherComplex() throws Exception {
		assertThat(new Complex(), new GenericObject() {
			public Matcher<Simple> simple = new GenericObject() {
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
}
