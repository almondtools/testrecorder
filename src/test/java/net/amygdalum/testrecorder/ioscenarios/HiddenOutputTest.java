package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.assertjconventions.Assertions.assertThat;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.HiddenOutput", "net.amygdalum.testrecorder.ioscenarios.Outputs" })
public class HiddenOutputTest {

	@Test
	public void testOutputImmediate() throws Exception {
		HiddenOutput output = new HiddenOutput();

		output.outputImmediate("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenOutput.class), compiles(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class), testsRun(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class)).containsWildcardPattern(".add(HiddenOutput.class, \"outputImmediate\", *, null, equalTo(\"Hello\")");
		assertThat(testGenerator.renderTest(HiddenOutput.class)).contains("verify()");
	}

	@Test
	public void testOutputWithUnexposedDependency() throws Exception {
		HiddenOutput output = new HiddenOutput();

		output.outputToField("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenOutput.class), compiles(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class), testsRun(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class)).containsWildcardPattern(".add(HiddenOutput.class, \"outputToField\", *, null, equalTo(\"Hello\")");
		assertThat(testGenerator.renderTest(HiddenOutput.class)).contains("verify()");
	}

}