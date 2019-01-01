package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {
	"net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput",
	"java.lang.System",
	"java.io.FileInputStream",
	"java.io.RandomAccessFile" }, config = StandardLibInputOutputTestRecorderAgentConfig.class)
public class StandardLibInputTest {

	@Test
	public void testNativeMethodCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testNativeMethodWithResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		int result = io.readFile(new byte[] { 41, 42 }, 1);

		assertThat(result).isEqualTo(42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testNativeMethodWithArgsAndResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		byte[] result = io.readFile(new byte[] { 41, 42 });

		assertThat(result).isEqualTo(new byte[] { 41, 42 });

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testNativeMethodWithArgsNoResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		byte[] result = io.readRandomAccessFile(new byte[] { 41, 42 });

		assertThat(result).isEqualTo(new byte[] { 41, 42 });

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

}
