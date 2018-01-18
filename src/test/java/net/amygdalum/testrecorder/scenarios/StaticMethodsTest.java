package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.testing.assertj.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.StaticMethods" })
public class StaticMethodsTest {

	@Test
	public void testCompilable() throws Exception {
		StaticMethods object = StaticMethods.from("str");

		assertThat(object.getValue()).isEqualTo("str");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StaticMethods.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		StaticMethods object = StaticMethods.from("str2");

		assertThat(object.getValue()).isEqualTo("str2");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(StaticMethods.class)).hasSize(1);
		assertThat(testGenerator.testsFor(StaticMethods.class)).iterate()
			.next().satisfies(test -> assertThat(test)
				.containsWildcardPattern("StaticMethods.from(\"str2\")")
				.doesNotContainPattern("net.amygdalum.testrecorder.scenarios.StaticMethods.from"));
	}

}