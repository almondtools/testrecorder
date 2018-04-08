package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Results" })
public class ResultsTest {

	@Test
	public void testNumberOfGeneratedTests() throws Exception {
		List<Double> results = new ArrayList<>();
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			results.add(pow.pow(i));
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(results).hasSize(10);
		assertThat(testGenerator.testsFor(Results.class)).hasSize(10);
	}

	@Test
	public void testAssertsInEachTest() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Results.class)).allSatisfy(test -> assertThat(test).contains("assert"));
	}

	@Test
	public void testCompilesAndRuns() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Results.class)).satisfies(testsRun());
	}

}