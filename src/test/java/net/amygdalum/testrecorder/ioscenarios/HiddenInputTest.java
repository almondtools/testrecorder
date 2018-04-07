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
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.HiddenInput", "net.amygdalum.testrecorder.ioscenarios.Inputs" })
public class HiddenInputTest {

	@Test
	public void testInputImmediate() throws Exception {
		HiddenInput input = new HiddenInput();

		String result = input.inputImmediate();

		assertThat(result).isEqualTo("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInput.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(HiddenInput.class).getTestCode())
			.containsWildcardPattern(".addFreeVirtual(*, \"Hello\")");
	}

	@Test
	public void testInputWithUnexposedDependency() throws Exception {
		HiddenInput input = new HiddenInput();

		String result = input.inputFromField();

		assertThat(result).isEqualTo("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInput.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(HiddenInput.class).getTestCode())
			.containsWildcardPattern(".addVirtual(inputs?, \"Hello\")");
	}

}