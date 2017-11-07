package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.HiddenOutput", "net.amygdalum.testrecorder.scenarios.Outputs" })
public class HiddenOutputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testOutputImmediate() throws Exception {
		HiddenOutput output = new HiddenOutput();

		output.outputImmediate("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenOutput.class), compiles(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class), testsRun(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class), containsString(".expect(\"print\""));
		assertThat(testGenerator.renderTest(HiddenOutput.class), containsString("verifies()"));
	}

	@Test
	public void testOutputWithUnexposedDependency() throws Exception {
		HiddenOutput output = new HiddenOutput();

		output.outputToField("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenOutput.class), compiles(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class), testsRun(HiddenOutput.class));
		assertThat(testGenerator.renderTest(HiddenOutput.class), containsString(".expect(\"print\""));
		assertThat(testGenerator.renderTest(HiddenOutput.class), containsString("verifies()"));
	}

}