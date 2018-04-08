package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Globals" })
public class GlobalsTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		Globals.global = 1;

		Globals.incGlobal();

		assertThat(Globals.global).isEqualTo(2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Globals.class)).hasSize(1);
		assertThat(testGenerator.renderTest(Globals.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		Globals.global = 1;

		Globals.incGlobal();

		assertThat(Globals.global).isEqualTo(2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Globals.class).getTestCode())
			.doesNotContain("net.amygdalum.testrecorder.scenarios.Globals.incGlobal")
			.contains("Globals.global = 1;")
			.doesNotContain("net.amygdalum.testrecorder.scenarios.Globals.global = 1;")
			.contains("Globals.global, equalTo(2)")
			.doesNotContain("net.amygdalum.testrecorder.scenarios.Globals.global, equalTo(2);")
			.doesNotContainWildcardPattern("Globals.global = 1;*Globals.global = 1;")
			.doesNotContainWildcardPattern("Globals.global, equalTo(2)*Globals.global, equalTo(2)");
	}

}