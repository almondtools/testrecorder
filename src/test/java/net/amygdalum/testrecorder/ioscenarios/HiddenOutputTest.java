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
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.HiddenOutput", "net.amygdalum.testrecorder.ioscenarios.Outputs" })
public class HiddenOutputTest {

	@Test
	public void testOutputImmediate() throws Exception {
		HiddenOutput output = new HiddenOutput();

		output.outputImmediate("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenOutput.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(HiddenOutput.class).getTestCode())
			.containsWildcardPattern(".addFreeVirtual(*, null, equalTo(\"Hello\")")
			.contains("verify()");
	}

	@Test
	public void testOutputWithUnexposedDependency() throws Exception {
		HiddenOutput output = new HiddenOutput();

		output.outputToField("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenOutput.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(HiddenOutput.class).getTestCode())
			.containsWildcardPattern(".addVirtual(outputs?, null, equalTo(\"Hello\")")
			.contains("verify()");
	}

}