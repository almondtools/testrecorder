package net.amygdalum.testrecorder.ioscenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.HiddenInput", "net.amygdalum.testrecorder.ioscenarios.Inputs" })
public class HiddenInputTest {

	

	@Test
	public void testInputImmediate() throws Exception {
		HiddenInput input = new HiddenInput();

		String result = input.inputImmediate();

		assertThat(result).isEqualTo("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInput.class), compiles(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), testsRun(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), containsPattern(".add(HiddenInput.class, \"inputImmediate\", *, \"Hello\")"));
	}

	@Test
	public void testInputWithUnexposedDependency() throws Exception {
		HiddenInput input = new HiddenInput();

		String result = input.inputFromField();

		assertThat(result).isEqualTo("Hello");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInput.class), compiles(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), testsRun(HiddenInput.class));
		assertThat(testGenerator.renderTest(HiddenInput.class), containsPattern(".add(HiddenInput.class, \"inputFromField\", *, \"Hello\")"));
	}

}