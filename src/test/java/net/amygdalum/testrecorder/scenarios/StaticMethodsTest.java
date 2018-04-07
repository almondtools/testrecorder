package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.extensions.assertj.iterables.IterableConditions.containingExactly;
import static net.amygdalum.extensions.assertj.strings.StringConditions.containing;
import static net.amygdalum.extensions.assertj.strings.StringConditions.containingWildcardPattern;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.condition.AllOf.allOf;
import static org.assertj.core.condition.Not.not;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

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
		assertThat(testGenerator.testsFor(StaticMethods.class)).is(containingExactly(
			allOf(
				containingWildcardPattern("StaticMethods.from(\"str2\")"),
				not(containing("net.amygdalum.testrecorder.scenarios.StaticMethods.from")))));
	}

}