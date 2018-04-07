package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes={"net.amygdalum.testrecorder.ioscenarios.DelegatedInput", "net.amygdalum.testrecorder.ioscenarios.DelegatedInput$SingleInput"})
public class DelegatedInputTest {
	
	@Test
	public void testCompilesAndRuns() throws Exception {
		DelegatedInput.SingleInput singleInput = new DelegatedInput.SingleInput();
		DelegatedInput input1 = new DelegatedInput(singleInput);
		DelegatedInput input2 = new DelegatedInput(singleInput);
		input1.combine(input2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DelegatedInput.class)).satisfies(testsRun());
	}
	
}
