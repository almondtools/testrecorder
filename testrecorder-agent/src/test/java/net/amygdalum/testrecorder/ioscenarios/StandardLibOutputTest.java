package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.test.TestsRun;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput", "java.io.OutputStream",
	"java.io.ByteArrayOutputStream" }, config = StandardLibInputOutputTestRecorderAgentConfig.class)
public class StandardLibOutputTest {

	@Test
	public void testJavaMethodCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.store("My Output");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(TestsRun.testsRun());
	}

	@Test
	public void testNativeMethodCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.sleep();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

}
