package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.ContainsInOrderMatcher.containsInOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class ContainsInOrderMatcherTest {

	@Test
	public void testDescribeTo() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, "A", "b").describeTo(description);

		assertThat(description.toString()).isEqualTo("containing in sequence [<\"A\">, <\"b\">]");
	}

	@Test
	public void testMatchesSafelyMatchers() throws Exception {
		assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(asList("A"))).isTrue();
		assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(asList("b"))).isFalse();
		assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(emptyList())).isFalse();
	}

	@Test
	public void testMatchesSafelyWithSuccess() throws Exception {
		assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A", "b"))).isTrue();
		assertThat(containsInOrder(String.class, "A", null).matchesSafely(asList("A", null))).isTrue();
	}

	@Test
	public void testMatchesSafelyWithFailure() throws Exception {
		assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A"))).isFalse();
		assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("a", "b"))).isFalse();
		assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A", "b", "C"))).isFalse();
	}

	@Test
	public void testDescribeMismatchSafelyNotEqual() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("a", "b"), description);

		assertThat(description.toString()).containsWildcardPattern(""
			+ "mismatching elements <[*"
			+ "was \"a\",*"
			+ "."
			+ "]>");
	}

	@Test
	public void testDescribeMismatchSafelyNull() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, "A", null).describeMismatchSafely(asList("A", "b"), description);

		assertThat(description.toString()).containsWildcardPattern(""
			+ "mismatching elements <[*"
			+ ".,*"
			+ "was \"b\""
			+ "]>");
	}

	@Test
	public void testDescribeMismatchSafelyToMany() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A", "b", "c"), description);

		assertThat(description.toString()).containsWildcardPattern(""
			+ "mismatching elements <[*"
			+ "..,*"
			+ "was \"c\"*"
			+ "]>");
	}

	@Test
	public void testDescribeMismatchSafelyToFew() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A"), description);

		assertThat(description.toString()).containsWildcardPattern(""
			+ "mismatching elements <[*"
			+ ".,*"
			+ "missing 1 elements*"
			+ "]>");
	}

	@Test
	public void testDescribeMismatchSafelyMatching() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A", "b"), description);

		assertThat(description.toString()).isEmpty();
	}

	@Test
	public void testDescribeMismatchSafelyNullExpected() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, nullValue()).describeMismatchSafely(asList("A"), description);

		assertThat(description.toString()).containsWildcardPattern(""
			+ "mismatching elements <["
			+ "was \"A\""
			+ "]>");
	}

	@Test
	public void testDescribeMismatchSafelyNullMissing() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, nullValue()).describeMismatchSafely(asList(null, "B"), description);

		assertThat(description.toString()).containsWildcardPattern(""
			+ "mismatching elements <["
			+ "., found 1 elements surplus [was \"B\"]"
			+ "]>");
	}

}
