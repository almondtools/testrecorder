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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.StaticMethodAndState" })
public class StaticMethodAndStateTest {

	@Test
	public void testCompilableSettingFromNull() throws Exception {
		StaticMethodAndState.global = null;

		StaticMethodAndState.setGlobal("str");

		assertThat(StaticMethodAndState.global).isEqualTo("str");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StaticMethodAndState.class)).satisfies(testsRun());
	}

	@Test
	public void testCompilableSettingToNull() throws Exception {
		StaticMethodAndState.global = "str";

		StaticMethodAndState.setGlobal(null);

		assertThat(StaticMethodAndState.global).isNull();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StaticMethodAndState.class)).satisfies(testsRun());
	}

}