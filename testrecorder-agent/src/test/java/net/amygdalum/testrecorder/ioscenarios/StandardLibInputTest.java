package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput", "java.lang.System",
	"java.io.FileInputStream" }, config = StandardLibInputOutputTestRecorderAgentConfig.class)
public class StandardLibInputTest {

	@Test
	public void testNativeMethodCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testNativeMethodWithArgsCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		int result = io.readFile(new byte[] { 41, 42 }, 1);

		assertThat(result).isEqualTo(42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

}
