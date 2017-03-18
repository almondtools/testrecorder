package net.amygdalum.testrecorder.dynamiccompile;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.StringDescription;
import org.junit.Test;

public class TestsRunnableMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();

        testsRun(TestsRunnableMatcherTest.class).describeTo(description);

        assertThat(description.toString(), equalTo("should compile and run with success"));
    }

    @Test
    public void testMatchesSafelyWithSuccess() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.Test;"
            + ""
            + "public class SuccessTest {"
            + "  @Test"
            + "  public void test() throws Exception {"
            + "  }"
            + "}", description);

        assertThat(matches, is(true));
        assertThat(description.toString(), equalTo(""));
    }

    @Test
    public void testMatchesSafelyWithTestFailure() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.Test;"
            + "import static org.junit.Assert.fail;"
            + ""
            + "public class SuccessTest {"
            + "  @Test"
            + "  public void test() throws Exception {"
            + "    fail();"
            + "  }"
            + "}", description);

        assertThat(matches, is(false));
        assertThat(description.toString(), containsPattern(""
            + "compiled successfully but got test failures : 1*"
            + "- AssertionError: null"));
    }

    @Test
    public void testMatchesSafelyWithCompileFailure() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.Test;"
            + "import static org.junit.Assert.fail;"
            + ""
            + "public class SuccessTest {"
            + "  @Test"
            + "  public void test() throws Exception {"
            + "    fail() // missing semicolon"
            + "  }"
            + "}", description);

        assertThat(matches, is(false));
        assertThat(description.toString(), containsPattern(""
            + "compile failed with messages"));
    }

    @Test
    public void testMatchesSafelyWithError() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = testsRun(TestsRunnableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.Test;"
            + ""
            + "public class SuccessTest {"
            + "  @Test"
            + "  public void test() throws Exception {"
            + "    throw new RuntimeException();"
            + "  }"
            + "}", description);
        
        assertThat(matches, is(false));
        assertThat(description.toString(), containsPattern(""
            + "compiled successfully but got test failures : 1*"
            + "- RuntimeException: null"));
    }

}
