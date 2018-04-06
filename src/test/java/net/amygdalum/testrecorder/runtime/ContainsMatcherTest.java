package net.amygdalum.testrecorder.runtime;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.ContainsMatcher.contains;
import static net.amygdalum.testrecorder.runtime.ContainsMatcher.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class ContainsMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeTo(description);

        assertThat(description.toString()).isEqualTo("containing [<\"A\">, <\"b\">]");
    }

    @Test
    public void testMatchesSafelyEmpty() throws Exception {
        assertThat(empty(String.class).matchesSafely(asList("A", "b"))).isFalse();
        assertThat(empty(String.class).matchesSafely(emptySet())).isTrue();
    }

    @Test
    public void testMatchesSafelyMatchers() throws Exception {
        assertThat(contains(String.class, equalTo("A")).matchesSafely(asList("A"))).isTrue();
        assertThat(contains(String.class, equalTo("A")).matchesSafely(asList("b"))).isFalse();
        assertThat(contains(String.class, equalTo("A")).matchesSafely(emptyList())).isFalse();
    }

    @Test
    public void testMatchesSafelyWithSuccess() throws Exception {
        assertThat(contains(String.class, "A", "b").matchesSafely(asList("A", "b"))).isTrue();
        assertThat(contains(String.class, "A", null).matchesSafely(asList("A", null))).isTrue();
    }

    @Test
    public void testMatchesSafelyWithFailure() throws Exception {
        boolean matches = contains(String.class, "A", "b").matchesSafely(asList("a", "b"));

        assertThat(matches).isFalse();
    }

    @Test
    public void testDescribeMismatchSafelyNotEqual() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeMismatchSafely(asList("a", "b"), description);

        assertThat(description.toString()).containsWildcardPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "found 1 elements surplus [was \"a\"],*"
            + "missing 1 elements*"
            + "]>");
    }

    @Test
    public void testDescribeMismatchSafelyNull() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", null).describeMismatchSafely(asList("A", "b"), description);

        assertThat(description.toString()).containsWildcardPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "found 1 elements surplus [was \"b\"],*"
            + "missing 1 elements*"
            + "]>");
    }

    @Test
    public void testDescribeMismatchSafelyToMany() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeMismatchSafely(asList("A", "b", "c"), description);

        assertThat(description.toString()).containsWildcardPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "found 1 elements surplus [was \"c\"]*"
            + "]>");
    }

    @Test
    public void testDescribeMismatchSafelyToFew() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeMismatchSafely(asList("A"), description);

        assertThat(description.toString()).containsWildcardPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "missing 1 elements*"
            + "]>");
    }

    @Test
    public void testDescribeMismatchSafelyNullOnly() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, nullValue()).describeMismatchSafely(asList("A"), description);

        assertThat(description.toString()).containsWildcardPattern(""
            + "mismatching elements <["
            + "found 1 elements surplus [was \"A\"]*"
            + "missing 1 elements"
            + "]>");
    }

}
