package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ExcludedFromChecking" })
public class ExcludedFromCheckingTest {

    @Before
    public void before() throws Exception {
        TestGenerator.fromRecorded().clearResults();
    }

    @Test
    public void testFieldsExcludedInTestCompilable() throws Exception {
        ExcludedFromChecking arrays = new ExcludedFromChecking(42);

        arrays.next();

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.testsFor(ExcludedFromChecking.class), hasSize(1));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), compiles(LargeIntArrays.class));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), testsRun(LargeIntArrays.class));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), allOf(
            containsPattern("assertThat(long*, equalTo(84l))"),
            containsString("int intVar = 42;"),
            not(containsString("long longVar = 84l;"))));
    }

    @Test
    public void testResultsExcludedInTestCompilable() throws Exception {
        ExcludedFromChecking arrays = new ExcludedFromChecking(42);

        arrays.getIntVar();

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.testsFor(ExcludedFromChecking.class), hasSize(1));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), compiles(LargeIntArrays.class));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), testsRun(LargeIntArrays.class));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), not(containsPattern("assertThat(int*, equalTo(1764))")));
    }

    @Test
    public void testArgumentsExcludedInTestCompilable() throws Exception {
        ExcludedFromChecking arrays = new ExcludedFromChecking(42);

        arrays.reinit(12,45,78);

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.testsFor(ExcludedFromChecking.class), hasSize(1));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), compiles(LargeIntArrays.class));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), testsRun(LargeIntArrays.class));
        assertThat(testGenerator.renderTest(ExcludedFromChecking.class), not(containsPattern("assertThat(intArray*, intArrayContaining(12, 45, 78))")));
    }
}