package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.ImplementedInput", "net.amygdalum.testrecorder.ioscenarios.InterfacedInput" })
public class ImplementedInputTest {

	@Test
	public void testRunsAndCompiles() throws Exception {
		ImplementedInput in = new ImplementedInput();
		in.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ImplementedInput.class)).hasSize(1);
		assertThat(testGenerator.renderTest(ImplementedInput.class)).satisfies(testsRun());
	}

	@Test
	public void testFakesInput() throws Exception {
		ImplementedInput in = new ImplementedInput();
		in.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ImplementedInput.class).getTestCode())
			.containsWildcardPattern("FakeIO.fake(ImplementedInput.class)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public long input() {*}*})")
			.containsWildcardPattern(".addVirtual(implementedInput?, *)");
		assertThat(testGenerator.renderTest(ImplementedInput.class)).satisfies(testsRun());
	}

}