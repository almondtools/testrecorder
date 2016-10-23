package net.amygdalum.testrecorder.util;

import static net.amygdalum.xrayinterface.IsEquivalent.equivalentTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;

@SuppressWarnings("unused")
public class GenericMatcherTest {

	@Test
	public void testMatchesSimple() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.mismatchesWith(null, new Simple("myStr")), empty());
	}

	@Test
	public void testMatchingSimple() throws Exception {
		assertThat(new Simple("myStr"), new GenericMatcher() {
			public String str = "myStr";

		}.matching(Simple.class));
	}

	@Test
	public void testMatchesComplex() throws Exception {
		assertThat(new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matching(Simple.class);
		}.mismatchesWith(null, new Complex()), empty());
	}

	@Test
	public void testMatchingNullMatcher() throws Exception {
		assertThat(new GenericMatcher() {
			Matcher<?> str = nullValue();
		}.mismatchesWith(null, new Simple()), empty());
	}

	@Test
	public void testMatchingNullValue() throws Exception {
		assertThat(new GenericMatcher() {
			String str = null;
		}.mismatchesWith(null, new Simple()), empty());
	}

	@Test
	public void testMatchingComplex() throws Exception {
		assertThat(new Complex(), new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matching(Simple.class);
		}.matching(Complex.class));
	}

	@Test
	public void testMismatchesSimple() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";
		}.mismatchesWith(null, new Simple("notMyStr")), contains(equivalentTo(GenericComparisonMatcher.class)
			.withLeft("myStr")
			.withRight("notMyStr")));
	}

	interface GenericComparisonMatcher extends Matcher<GenericComparison> {

		GenericComparisonMatcher withLeft(Object left);

		GenericComparisonMatcher withRight(Object right);
	}
}
