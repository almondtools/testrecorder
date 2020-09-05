package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.util.Debug;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {
	"net.amygdalum.testrecorder.ioscenarios.LoggerOutput",
	"java.util.logging.Logger"}, config = LoggerOutputTestRecorderAgentConfig.class)
public class LoggerOutputTest {

	@Test
	public void testDirectNativeMethodCompilesAndRuns() throws Exception {
		LoggerOutput io = new LoggerOutput();
		boolean isLogging = io.isLogging();

		assertThat(isLogging).isEqualTo(true);
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(Debug.print(testGenerator.renderTest(LoggerOutput.class).getTestCode())).containsSubsequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(LoggerOutput.class)).satisfies(testsRun());
	}
}
