package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.util.ContainsMatcher.contains;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.ContainingList;
import net.amygdalum.testrecorder.util.testobjects.ContainingSet;
import net.amygdalum.testrecorder.util.testobjects.Simple;

@SuppressWarnings("unused")
public class FailureTraceTest {

	@Test
	public void testFirstLevelDifferences() throws Exception {
		Matcher<Simple> matcher = new GenericMatcher() {
			public String str = "myStr";
		}.matching(Simple.class);

		assertThat(describeMismatch(matcher, new Simple("notMyStr")), containsString("str: \"myStr\" != \"notMyStr\""));
		assertThat(describeMismatch(matcher, new Simple("notMyStr")), not(containsString(".str: \"myStr\" != \"notMyStr\"")));
	}

	@Test
	public void testSecondLevelDifferences() throws Exception {
		Matcher<Complex> matcher = new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matching(Simple.class);
		}.matching(Complex.class);

		assertThat(describeMismatch(matcher, new Complex("notOtherStr")), containsString("simple.str: \"otherStr\" != \"notOtherStr\""));
	}

	@Test
	public void testSetDifferences() throws Exception {
		Matcher<ContainingSet> matcher = new GenericMatcher() {
			public Matcher<?> set = contains(String.class, "first", "second");
		}.matching(ContainingSet.class);

		assertThat(describeMismatch(matcher, new ContainingSet(asList("first", "notsecond"))), containsString("set: <[\"first\", \"second\"]> != <[first, notsecond]>"));
	}

	@Test
	public void testListDifferences() throws Exception {
		Matcher<ContainingList> matcher = new GenericMatcher() {
			public Matcher<?> list = contains(String.class, "first", "second");
		}.matching(ContainingList.class);

		assertThat(describeMismatch(matcher, new ContainingList(asList("first", "notsecond"))), containsString("list: <[\"first\", \"second\"]> != <[first, notsecond]>"));
	}

	private <T> String describeMismatch(Matcher<T> matcher, T object) {
		StringDescription description = new StringDescription();
		matcher.describeMismatch(object, description);
		return description.toString();
	}

}
