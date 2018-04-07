package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.NestedOutput" })
public class NestedOutputTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		NestedOutput input = new NestedOutput();
		int time = input.getTime();
		time = input.getTime();

		assertThat(time).isEqualTo(2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedOutput.class)).satisfies(testsRun());
	}

}
