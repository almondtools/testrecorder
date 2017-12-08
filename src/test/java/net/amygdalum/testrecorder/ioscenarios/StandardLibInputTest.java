package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput", "java.lang.System","java.io.FileInputStream" }, config = StandardLibInputOutputTestRecorderAgentConfig.class)
public class StandardLibInputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testNativeMethodCompilable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), allOf(
			containsString("FakeIO"),
			containsString("fakeInput")));
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), compiles(StandardLibInputOutput.class));
	}

	@Test
	public void testNativeMethodRunnable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), testsRun(StandardLibInputOutput.class));
	}

	@Test
	public void testNativeMethodWithArgsCompilable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		int result = io.readFile(new byte[] { 41, 42 }, 1);
		
		assertThat(result, equalTo(42));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), allOf(
			containsString("FakeIO"),
			containsString("fakeInput")));
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), compiles(StandardLibInputOutput.class));
	}

	@Test
	public void testNativeMethodWithArgsRunnable() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		int result = io.readFile(new byte[] { 41, 42 }, 1);
		
		assertThat(result, equalTo(42));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), testsRun(StandardLibInputOutput.class));
	}

}
