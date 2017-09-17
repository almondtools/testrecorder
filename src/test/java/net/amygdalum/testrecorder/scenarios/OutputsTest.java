package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Outputs" })
public class OutputsTest {

    @Before
    public void before() throws Exception {
        TestGenerator.fromRecorded().clearResults();
    }

    @Test
    public void testCompilableNotRecorded() throws Exception {
        Outputs out = new Outputs();
        out.notrecorded();

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.testsFor(Outputs.class), empty());
    }

    @Test
    public void testCompilable() throws Exception {
        Outputs out = new Outputs();
        out.recorded();

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(Outputs.class), compiles(Outputs.class));
    }

    @Test
    public void testCompilableConditionalReturn() throws Exception {
    	Outputs out = new Outputs();
    	out.recordedWithConditionalReturn();
    	
    	TestGenerator testGenerator = TestGenerator.fromRecorded();
    	assertThat(testGenerator.renderTest(Outputs.class), compiles(Outputs.class));
    }
    
    @Test
    public void testPrimitivesCompilable() throws Exception {
        Outputs out = new Outputs();
        out.primitivesRecorded();

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(Outputs.class), compiles(Outputs.class));
    }

    @Test
    public void testRunnable() throws Exception {
        Outputs out = new Outputs();
        out.recorded();

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.testsFor(Outputs.class), hasSize(1));
        assertThat(testGenerator.renderTest(Outputs.class), allOf(
            containsPattern(".expect(\"print\", new Class[]{String.class}, null, equalTo(\"Hello \")"),
            containsPattern(".expect(\"print\", new Class[]{String.class}, null, equalTo(\"World\")")));
        assertThat(testGenerator.renderTest(Outputs.class), containsString("verifies"));
        assertThat(testGenerator.renderTest(Outputs.class), testsRun(Outputs.class));
    }

    @Test
    public void testRunnableConditionalReturn() throws Exception {
    	Outputs out = new Outputs();
    	out.recordedWithConditionalReturn();
    	
    	TestGenerator testGenerator = TestGenerator.fromRecorded();
    	assertThat(testGenerator.testsFor(Outputs.class), hasSize(1));
    	assertThat(testGenerator.renderTest(Outputs.class), allOf(
    		containsPattern(".expect(\"conditionalReturnOutput\", new Class[]{char.class}, true, equalTo('a')"),
    		containsPattern(".expect(\"conditionalReturnOutput\", new Class[]{char.class}, true, equalTo(',')"),
    		containsPattern(".expect(\"conditionalReturnOutput\", new Class[]{char.class}, false, equalTo(' ')"),
    		containsPattern(".expect(\"conditionalReturnOutput\", new Class[]{char.class}, true, equalTo('b')"),
    		containsPattern(".expect(\"conditionalReturnOutput\", new Class[]{char.class}, false, equalTo('\\n')")));

        assertThat(testGenerator.renderTest(Outputs.class), containsString("verifies"));
    	assertThat(testGenerator.renderTest(Outputs.class), testsRun(Outputs.class));
    }
    
    @Test
    public void testPrimitivesRunnable() throws Exception {
        Outputs out = new Outputs();
        out.primitivesRecorded();

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(Outputs.class), containsString("verifies"));
        assertThat(testGenerator.renderTest(Outputs.class), testsRun(Outputs.class));
    }

}