package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static net.amygdalum.testrecorder.util.ContainsMatcher.contains;
import static net.amygdalum.testrecorder.util.ContainsMatcher.empty;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.StringDescription;
import org.junit.Test;

public class ContainsMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeTo(description);

        assertThat(description.toString(), equalTo("containing [<\"A\">, <\"b\">]"));
    }

    @Test
    public void testMatchesSafelyEmpty() throws Exception {
        assertThat(empty(String.class).matchesSafely(asList("A", "b")), is(false));
        assertThat(empty(String.class).matchesSafely(emptySet()), is(true));
    }

    @Test
    public void testMatchesSafelyMatchers() throws Exception {
        assertThat(contains(String.class, equalTo("A")).matchesSafely(asList("A")), is(true));
        assertThat(contains(String.class, equalTo("A")).matchesSafely(asList("b")), is(false));
        assertThat(contains(String.class, equalTo("A")).matchesSafely(emptyList()), is(false));
    }

    @Test
    public void testMatchesSafelyWithSuccess() throws Exception {
        assertThat(contains(String.class, "A", "b").matchesSafely(asList("A", "b")), is(true));
        assertThat(contains(String.class, "A", null).matchesSafely(asList("A", null)), is(true));
    }

    @Test
    public void testMatchesSafelyWithFailure() throws Exception {
        boolean matches = contains(String.class, "A", "b").matchesSafely(asList("a", "b"));

        assertThat(matches, is(false));
    }

    @Test
    public void testDescribeMismatchSafelyNotEqual() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeMismatchSafely(asList("a", "b"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "found 1 elements surplus [was \"a\"],*"
            + "missing 1 elements*"
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyNull() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", null).describeMismatchSafely(asList("A", "b"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "found 1 elements surplus [was \"b\"],*"
            + "missing 1 elements*"
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyToMany() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeMismatchSafely(asList("A", "b", "c"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "found 1 elements surplus [was \"c\"]*"
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyToFew() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, "A", "b").describeMismatchSafely(asList("A"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "missing 1 elements*"
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyNullOnly() throws Exception {
        StringDescription description = new StringDescription();

        contains(String.class, nullValue()).describeMismatchSafely(asList("A"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <["
            + "found 1 elements surplus [was \"A\"]*"
            + "missing 1 elements"
            + "]>"));
    }

}
