package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.Results"})
public class ResultsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testNumberOfGeneratedTests() throws Exception {
		List<Double> results = new ArrayList<>();
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			results.add(pow.pow(i));
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(results, hasSize(10));
		assertThat(testGenerator.testsFor(Results.class), hasSize(10));
	}

	@Test
	public void testAssertsInEachTest() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Results.class), everyItem(containsString("assert")));
	}

	@Test
	public void testCompilable() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Results.class), compiles(Results.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Results.class), testsRun(Results.class));
	}
	
}