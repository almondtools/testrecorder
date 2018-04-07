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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ExcludedFromChecking" })
public class ExcludedFromCheckingTest {

	@Test
	public void testFieldsExcludedInTestCompilable() throws Exception {
		ExcludedFromChecking arrays = new ExcludedFromChecking(42);

		arrays.next();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ExcludedFromChecking.class)).hasSize(1);
		assertThat(testGenerator.renderTest(ExcludedFromChecking.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(ExcludedFromChecking.class).getTestCode())
			.containsWildcardPattern("assertThat(long*, equalTo(84l))")
			.contains("int intVar = 42;")
			.doesNotContain("long longVar = 84l;");
	}

	@Test
	public void testResultsExcludedInTestCompilable() throws Exception {
		ExcludedFromChecking arrays = new ExcludedFromChecking(42);

		arrays.getIntVar();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ExcludedFromChecking.class)).hasSize(1);
		assertThat(testGenerator.renderTest(ExcludedFromChecking.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(ExcludedFromChecking.class).getTestCode()).doesNotContainWildcardPattern("assertThat(int*, equalTo(1764))");
	}

	@Test
	public void testArgumentsExcludedInTestCompilable() throws Exception {
		ExcludedFromChecking arrays = new ExcludedFromChecking(42);

		arrays.reinit(12, 45, 78);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ExcludedFromChecking.class)).hasSize(1);
		assertThat(testGenerator.renderTest(ExcludedFromChecking.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(ExcludedFromChecking.class).getTestCode()).doesNotContainWildcardPattern("assertThat(intArray*, intArrayContaining(12, 45, 78))");
	}
}