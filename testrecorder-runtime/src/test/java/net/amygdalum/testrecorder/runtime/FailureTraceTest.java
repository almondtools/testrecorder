package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.runtime.ContainsMatcher.contains;
import static org.assertj.core.api.Assertions.assertThat;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.runtime.GenericMatcher;
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

		assertThat(describeMismatch(matcher, new Simple("notMyStr"))).contains("str: \"myStr\" != \"notMyStr\"");
		assertThat(describeMismatch(matcher, new Simple("notMyStr"))).doesNotContain(".str: \"myStr\" != \"notMyStr\"");
	}

	@Test
	public void testSecondLevelDifferences() throws Exception {
		Matcher<Complex> matcher = new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matching(Simple.class);
		}.matching(Complex.class);

		assertThat(describeMismatch(matcher, new Complex("notOtherStr"))).contains("simple.str: \"otherStr\" != \"notOtherStr\"");
	}

	@Test
	public void testSetDifferences() throws Exception {
		Matcher<ContainingSet> matcher = new GenericMatcher() {
			public Matcher<?> set = contains(String.class, "first", "second");
		}.matching(ContainingSet.class);

		assertThat(describeMismatch(matcher, new ContainingSet(asList("first", "notsecond")))).contains("set: containing [<\"first\">, <\"second\">] != <[first, notsecond]>");
	}

	@Test
	public void testListDifferences() throws Exception {
		Matcher<ContainingList> matcher = new GenericMatcher() {
			public Matcher<?> list = contains(String.class, "first", "second");
		}.matching(ContainingList.class);

		assertThat(describeMismatch(matcher, new ContainingList(asList("first", "notsecond")))).contains("list: containing [<\"first\">, <\"second\">] != <[first, notsecond]>");
	}

	private <T> String describeMismatch(Matcher<T> matcher, T object) {
		StringDescription description = new StringDescription();
		matcher.describeMismatch(object, description);
		return description.toString();
	}

}
