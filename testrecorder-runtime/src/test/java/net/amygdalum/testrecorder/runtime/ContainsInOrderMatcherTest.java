package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.ContainsInOrderMatcher.containsInOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ContainsInOrderMatcherTest {

	@Test
	void testDescribeTo() throws Exception {
		StringDescription description = new StringDescription();

		containsInOrder(String.class, "A", "b").describeTo(description);

		assertThat(description.toString()).isEqualTo("containing in sequence [<\"A\">, <\"b\">]");
	}

	@Nested
	class testMatchesSafely {

		@Nested
		class usingTypedFactory {
			@Test
			void withSuccess() throws Exception {
				assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A", "b"))).isTrue();
				assertThat(containsInOrder(String.class, "A", null).matchesSafely(asList("A", null))).isTrue();
			}

			@Test
			void withFailure() throws Exception {
				assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A"))).isFalse();
				assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("a", "b"))).isFalse();
				assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A", "b", "C"))).isFalse();
			}

			@Test
			void onMatchers() throws Exception {
				assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(asList("A"))).isTrue();
				assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(asList("b"))).isFalse();
				assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(emptyList())).isFalse();
			}

		}

		@Nested
		@SuppressWarnings("unchecked")
		class usingErasedFactory {

			@Test
			void withSuccess() throws Exception {
				assertThat(containsInOrder("A", "b").matchesSafely(asList("A", "b"))).isTrue();
				assertThat(containsInOrder("A", null).matchesSafely(asList("A", null))).isTrue();
			}

			@Test
			void withFailure() throws Exception {
				assertThat(containsInOrder("A", "b").matchesSafely(asList("A"))).isFalse();
				assertThat(containsInOrder("A", "b").matchesSafely(asList("a", "b"))).isFalse();
				assertThat(containsInOrder("A", "b").matchesSafely(asList("A", "b", "C"))).isFalse();
			}

			@Test
			void onMatchers() throws Exception {
				assertThat(containsInOrder(equalTo("A")).matchesSafely(asList("A"))).isTrue();
				assertThat(containsInOrder(equalTo("A")).matchesSafely(asList("b"))).isFalse();
				assertThat(containsInOrder(equalTo("A")).matchesSafely(emptyList())).isFalse();
			}

		}

		@Nested
		class testDescribeMismatchSafely {

			@Test
			void onNotEqual() throws Exception {
				StringDescription description = new StringDescription();

				containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("a", "b"), description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "mismatching elements <[*"
					+ "was \"a\",*"
					+ "."
					+ "]>");
			}

			@Test
			void onNull() throws Exception {
				StringDescription description = new StringDescription();

				containsInOrder(String.class, "A", null).describeMismatchSafely(asList("A", "b"), description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "mismatching elements <[*"
					+ ".,*"
					+ "was \"b\""
					+ "]>");
			}

			@Test
			void onToMany() throws Exception {
				StringDescription description = new StringDescription();

				containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A", "b", "c"), description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "mismatching elements <[*"
					+ "..,*"
					+ "was \"c\"*"
					+ "]>");
			}

			@Test
			void onToFew() throws Exception {
				StringDescription description = new StringDescription();

				containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A"), description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "mismatching elements <[*"
					+ ".,*"
					+ "missing 1 elements*"
					+ "]>");
			}

			@Test
			void onMatching() throws Exception {
				StringDescription description = new StringDescription();

				containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A", "b"), description);

				assertThat(description.toString()).isEmpty();
			}

			@Test
			void onNullExpected() throws Exception {
				StringDescription description = new StringDescription();

				containsInOrder(String.class, nullValue()).describeMismatchSafely(asList("A"), description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "mismatching elements <["
					+ "was \"A\""
					+ "]>");
			}

			@Test
			void onNullMissing() throws Exception {
				StringDescription description = new StringDescription();

				containsInOrder(String.class, nullValue()).describeMismatchSafely(asList(null, "B"), description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "mismatching elements <["
					+ "., found 1 elements surplus [was \"B\"]"
					+ "]>");
			}
		}
	}
}
