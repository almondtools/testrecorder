package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ShadowedObject", "net.amygdalum.testrecorder.scenarios.ShadowingObject" })
public class ShadowedObjectTest {

    @Before
    public void before() throws Exception {
        TestGenerator.fromRecorded().clearResults();
    }

    @Test
    public void testCompilable() throws Exception {
        ShadowingObject object = new ShadowingObject("field", 42);

        assertThat(object.toString(), equalTo("field42"));

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(ShadowingObject.class), compiles(ShadowingObject.class));
        assertThat(testGenerator.renderTest(ShadowingObject.class), testsRun(ShadowingObject.class));
    }

    @Test
    public void testCode() throws Exception {
        ShadowingObject object = new ShadowingObject("field", 42);

        assertThat(object.toString(), equalTo("field42"));

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(ShadowingObject.class), compiles(ShadowingObject.class));
        assertThat(testGenerator.renderTest(ShadowingObject.class), testsRun(ShadowingObject.class));
    }

}
