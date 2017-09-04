package net.amygdalum.testrecorder.runtime;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.runtime.ContainsInOrderMatcher.containsInOrder;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.StringDescription;
import org.junit.Test;

public class ContainsInOrderMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, "A", "b").describeTo(description);

        assertThat(description.toString(), equalTo("containing in sequence [<\"A\">, <\"b\">]"));
    }

    @Test
    public void testMatchesSafelyMatchers() throws Exception {
        assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(asList("A")), is(true));
        assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(asList("b")), is(false));
        assertThat(containsInOrder(String.class, equalTo("A")).matchesSafely(emptyList()), is(false));
    }

    @Test
    public void testMatchesSafelyWithSuccess() throws Exception {
        assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A", "b")), is(true));
        assertThat(containsInOrder(String.class, "A", null).matchesSafely(asList("A", null)), is(true));
    }

    @Test
    public void testMatchesSafelyWithFailure() throws Exception {
        assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A")), is(false));
        assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("a", "b")), is(false));
        assertThat(containsInOrder(String.class, "A", "b").matchesSafely(asList("A", "b", "C")), is(false));
    }

    @Test
    public void testDescribeMismatchSafelyNotEqual() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("a", "b"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + "was \"a\",*"
            + "."
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyNull() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, "A", null).describeMismatchSafely(asList("A", "b"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "was \"b\""
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyToMany() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A", "b", "c"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + "..,*"
            + "was \"c\"*"
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyToFew() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <[*"
            + ".,*"
            + "missing 1 elements*"
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyMatching() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, "A", "b").describeMismatchSafely(asList("A", "b"), description);

        assertThat(description.toString(), equalTo(""));
    }

    @Test
    public void testDescribeMismatchSafelyNullExpected() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, nullValue()).describeMismatchSafely(asList("A"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <["
            + "was \"A\""
            + "]>"));
    }

    @Test
    public void testDescribeMismatchSafelyNullMissing() throws Exception {
        StringDescription description = new StringDescription();

        containsInOrder(String.class, nullValue()).describeMismatchSafely(asList(null, "B"), description);

        assertThat(description.toString(), containsPattern(""
            + "mismatching elements <["
            + "., found 1 elements surplus [was \"B\"]"
            + "]>"));
    }

}
