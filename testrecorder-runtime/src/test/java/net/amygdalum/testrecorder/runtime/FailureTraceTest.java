package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.ContainsMatcher.contains;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.ContainingList;
import net.amygdalum.testrecorder.util.testobjects.ContainingSet;
import net.amygdalum.testrecorder.util.testobjects.Simple;

@SuppressWarnings("unused")
public class FailureTraceTest {

	@Nested
	class testDescribeMismatch {
		@Test
		void withFirstLevelDifferences() throws Exception {
			Matcher<Simple> matcher = new GenericMatcher() {
				public String str = "myStr";
			}.matching(Simple.class);

			assertThat(describeMismatch(matcher, new Simple("notMyStr"))).contains("str: \"myStr\" != \"notMyStr\"");
			assertThat(describeMismatch(matcher, new Simple("notMyStr"))).doesNotContain(".str: \"myStr\" != \"notMyStr\"");
		}

		@Test
		void withSecondLevelDifferences() throws Exception {
			Matcher<Complex> matcher = new GenericMatcher() {
				public Matcher<Simple> simple = new GenericMatcher() {
					public String str = "otherStr";
				}.matching(Simple.class);
			}.matching(Complex.class);

			assertThat(describeMismatch(matcher, new Complex("notOtherStr"))).contains("simple.str: \"otherStr\" != \"notOtherStr\"");
		}

		@Test
		void withSetDifferences() throws Exception {
			Matcher<ContainingSet> matcher = new GenericMatcher() {
				public Matcher<?> set = contains(String.class, "first", "second");
			}.matching(ContainingSet.class);

			assertThat(describeMismatch(matcher, new ContainingSet(asList("first", "notsecond"))))
				.containsWildcardPattern("set*found 1 elements surplus [was \"notsecond\"]")
				.containsWildcardPattern("set*missing 1 elements [\"second\"]");
		}

		@Test
		void withListDifferences() throws Exception {
			Matcher<ContainingList> matcher = new GenericMatcher() {
				public Matcher<?> list = contains(String.class, "first", "second");
			}.matching(ContainingList.class);

			assertThat(describeMismatch(matcher, new ContainingList(asList("first", "notsecond"))))
				.containsWildcardPattern("list*found 1 elements surplus [was \"notsecond\"]")
				.containsWildcardPattern("list*missing 1 elements [\"second\"]");
		}
	}

	private <T> String describeMismatch(Matcher<T> matcher, T object) {
		StringDescription description = new StringDescription();
		matcher.describeMismatch(object, description);
		return description.toString();
	}

}
