package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput", "java.io.OutputStream", "java.io.ByteArrayOutputStream" }, config = StandardLibInputOutputTestRecorderAgentConfig.class)
public class StandardLibOutputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testJavaMethodCompilable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.store("My Output");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), allOf(
			containsString("FakeIO"),
			containsString("fakeOutput")));
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), compiles(StandardLibInputOutput.class));
	}

	@Test
	public void testJavaMethodRunnable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.store("My Output");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), testsRun(StandardLibInputOutput.class));
	}

	@Test
	public void testNativeMethodCompilable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.sleep();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), allOf(
			containsString("FakeIO"),
			containsString("fakeOutput")));
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), compiles(StandardLibInputOutput.class));
	}

	@Test
	public void testNativeMethodRunnable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.sleep();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), testsRun(StandardLibInputOutput.class));
	}

}
