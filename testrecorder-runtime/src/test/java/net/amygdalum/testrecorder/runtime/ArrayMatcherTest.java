package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.ArrayMatcher.arrayContaining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ArrayMatcherTest {

	@Test
	void testDescribeTo() throws Exception {
		StringDescription description = new StringDescription();

		arrayContaining(String.class, "A", "b").describeTo(description);

		assertThat(description.toString()).isEqualTo("containing [<\"A\">, <\"b\">]");
	}

	@Nested
	class testMatchesSafely {

		@Test
		void withSuccess() throws Exception {
			assertThat(arrayContaining(String.class, "A", "b").matchesSafely(new String[] {"A", "b"})).isTrue();
			assertThat(arrayContaining(String.class, "A", null).matchesSafely(new String[] {"A", null})).isTrue();
		}

		@Test
		void withFailure() throws Exception {
			assertThat(arrayContaining(String.class, "A", "b").matchesSafely(new String[] {"a", "b"})).isFalse();
		}

		@Test
		void onEmpty() throws Exception {
			assertThat(arrayContaining(String.class).matchesSafely(new String[] {"A", "b"})).isFalse();
			assertThat(arrayContaining(String.class).matchesSafely(new String[0])).isTrue();
		}

		@Test
		void onMatchers() throws Exception {
			assertThat(arrayContaining(String.class, equalTo("A")).matchesSafely(new String[] {"A"})).isTrue();
			assertThat(arrayContaining(String.class, equalTo("A")).matchesSafely(new String[] {"b"})).isFalse();
			assertThat(arrayContaining(String.class, equalTo("A")).matchesSafely(new String[0])).isFalse();
		}
	}

	@Nested
	class testDescribeMismatchSafely {

		@Test
		void onNotEqual() throws Exception {
			StringDescription description = new StringDescription();

			arrayContaining(String.class, "A", "b").describeMismatchSafely(new String[] {"a", "b"}, description);

			assertThat(description.toString()).containsWildcardPattern(""
				+ "mismatching elements <[*"
				+ "was \"a\"*"
				+ "]>");
		}

		@Test
		void onEqual() throws Exception {
			StringDescription description = new StringDescription();

			arrayContaining(String.class, "A", "b").describeMismatchSafely(new String[] {"A", "b"}, description);

			assertThat(description.toString()).isEqualTo("");
		}

		@Test
		void onNull() throws Exception {
			StringDescription description = new StringDescription();

			arrayContaining(String.class, "A", null).describeMismatchSafely(new String[] {"A", "b"}, description);

			assertThat(description.toString()).containsWildcardPattern(""
				+ "mismatching elements <[., was \"b\"]>");
		}

		@Test
		void onTooMany() throws Exception {
			StringDescription description = new StringDescription();

			arrayContaining(String.class, "A", "b").describeMismatchSafely(new String[] {"A", "b", "c"}, description);

			assertThat(description.toString()).containsWildcardPattern(""
				+ "mismatching elements <[*"
				+ ".,*"
				+ "found 1 elements surplus [was \"c\"]*"
				+ "]>");
		}

		@Test
		void onTooFew() throws Exception {
			StringDescription description = new StringDescription();

			arrayContaining(String.class, "A", "b").describeMismatchSafely(new String[] {"A"}, description);

			assertThat(description.toString()).containsWildcardPattern(""
				+ "mismatching elements <[*"
				+ ".,*"
				+ "missing 1 elements*"
				+ "]>");
		}

		@Test
		void onNullOnly() throws Exception {
			StringDescription description = new StringDescription();

			arrayContaining(String.class, nullValue()).describeMismatchSafely(new String[] {"A"}, description);

			assertThat(description.toString()).containsWildcardPattern(""
				+ "mismatching elements <[was \"A\"]>");
		}

		@Test
		void onNullMore() throws Exception {
			StringDescription description = new StringDescription();

			arrayContaining(String.class, nullValue()).describeMismatchSafely(new String[] {null, "A"}, description);

			assertThat(description.toString()).containsWildcardPattern(""
				+ "mismatching elements <[., found 1 elements surplus [was \"A\"]]>");
		}
	}
}
