package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.NestedInput" })
public class NestedInputTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		NestedInput input = new NestedInput();
		String time = input.getTime();

		assertThat(time.matches("\\d+:\\d+")).isTrue();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedInput.class)).satisfies(testsRun());
	}

}
