package net.amygdalum.testrecorder.dynamiccompile;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class CompilableMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();

        compiles(CompilableMatcherTest.class).describeTo(description);

        assertThat(description.toString(), equalTo("should compile with success"));
    }

    @Test
    public void testMatchesSafelyWithSuccess() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.jupiter.api.Test;"
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
    public void testMatchesSafelyCached() throws Exception {
        StringDescription description = new StringDescription();
        
        CompilableMatcher matcher = compiles(CompilableMatcherTest.class);
        
        boolean matches = matcher.matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.jupiter.api.Test;"
            + ""
            + "public class SuccessTest {"
            + "  @Test"
            + "  public void test() throws Exception {"
            + "  }"
            + "}", description);
        matches = matcher.matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.jupiter.api.Test;"
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
    public void testMatchesSafelyCompilesWithFailingTest() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.jupiter.api.Test;"
            + "import static org.junit.Assert.fail;"
            + ""
            + "public class SuccessTest {"
            + "  @Test"
            + "  public void test() throws Exception {"
            + "    fail();"
            + "  }"
            + "}", description);

        assertThat(matches, is(true));
    }

    @Test
    public void testMatchesSafelyWithCompileFailure() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.jupiter.api.Test;"
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
    public void testMatchesSafelyCompilesWithTestError() throws Exception {
        StringDescription description = new StringDescription();

        boolean matches = compiles(CompilableMatcherTest.class).matchesSafely(""
            + "package net.amygdalum.testrecorder.dynamiccompile;"
            + "import org.junit.jupiter.api.Test;"
            + ""
            + "public class SuccessTest {"
            + "  @Test"
            + "  public void test() throws Exception {"
            + "    throw new RuntimeException();"
            + "  }"
            + "}", description);
        
        assertThat(matches, is(true));
    }

}
