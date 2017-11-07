package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.HiddenInput", "net.amygdalum.testrecorder.scenarios.Inputs" })
public class HiddenInputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testInputImmediate() throws Exception {
		HiddenInput input = new HiddenInput();

		String result = input.inputImmediate();

		assertThat(result, equalTo("Hello"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInput.class), compiles(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), testsRun(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), containsString(".provide(\"read\""));
	}

	@Test
	public void testInputWithUnexposedDependency() throws Exception {
		HiddenInput input = new HiddenInput();

		String result = input.inputFromField();

		assertThat(result, equalTo("Hello"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInput.class), compiles(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), testsRun(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), containsString(".provide(\"read\""));
	}

}