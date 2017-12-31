package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes={"net.amygdalum.testrecorder.ioscenarios.DelegatedInput", "net.amygdalum.testrecorder.ioscenarios.DelegatedInput$SingleInput"})
public class DelegatedInputTest {
	
	@Test
	public void testCompilable() throws Exception {
		DelegatedInput.SingleInput singleInput = new DelegatedInput.SingleInput();
		DelegatedInput input1 = new DelegatedInput(singleInput);
		DelegatedInput input2 = new DelegatedInput(singleInput);
		input1.combine(input2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DelegatedInput.class), compiles(DelegatedInput.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
		DelegatedInput.SingleInput singleInput = new DelegatedInput.SingleInput();
		DelegatedInput input1 = new DelegatedInput(singleInput);
		DelegatedInput input2 = new DelegatedInput(singleInput);
		input1.combine(input2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DelegatedInput.class), testsRun(DelegatedInput.class));
	}
	
}
