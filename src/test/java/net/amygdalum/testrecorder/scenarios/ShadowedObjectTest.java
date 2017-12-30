package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {
    "net.amygdalum.testrecorder.scenarios.ShadowedObject",
    "net.amygdalum.testrecorder.scenarios.ShadowingObject",
    "net.amygdalum.testrecorder.scenarios.Other$ShadowingObject"
})
public class ShadowedObjectTest {

    

    @Test
    public void testCompilable() throws Exception {
        ShadowingObject object = new ShadowingObject("field");

        assertThat(object.toString(), equalTo("field > 42"));

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(ShadowingObject.class), compiles(ShadowingObject.class));
        assertThat(testGenerator.renderTest(ShadowingObject.class), testsRun(ShadowingObject.class));
    }

    @Test
    public void testCode() throws Exception {
        ShadowingObject object = new ShadowingObject("field");

        assertThat(object.toString(), equalTo("field > 42"));

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.testsFor(ShadowingObject.class), hasSize(1));
        assertThat(testGenerator.testsFor(ShadowingObject.class), contains(containsPattern(""
            + "new GenericObject() {*"
            + "ShadowedObject$field*42*"
            + "ShadowingObject$field*\"field\"*"
            + "}")));
    }

    @Test
    public void testCodeDoubleHidden() throws Exception {
        Other.ShadowingObject object = new Other.ShadowingObject(42);

        assertThat(object.toString(), equalTo("42 > field > 42"));

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.testsFor(Other.class), hasSize(2));
        assertThat(testGenerator.testsFor(Other.class), hasItem(containsPattern(""
            + "new GenericObject() {*"
            + "int net$amygdalum$testrecorder$scenarios$Other$ShadowingObject$field = 42;*"
            + "int net$amygdalum$testrecorder$scenarios$ShadowedObject$field = 42;*"
            + "String net$amygdalum$testrecorder$scenarios$ShadowingObject$field = \"field\";*"
            + "}")));
    }

}
