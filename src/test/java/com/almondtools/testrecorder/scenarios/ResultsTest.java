package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.ConfigRegistry;
import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.TestGenerator;

public class ResultsTest {

	private static SnapshotInstrumentor instrumentor;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor(new DefaultConfig());
		instrumentor.register("com.almondtools.testrecorder.scenarios.Results");
	}
	
	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getMethodConsumer()).clearResults();
	}
	
	@Test
	public void testNumberOfGeneratedTests() throws Exception {
		List<Double> results = new ArrayList<>();
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			results.add(pow.pow(i));
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(pow);
		assertThat(results, hasSize(10));
		assertThat(testGenerator.testsFor(Results.class), hasSize(10));
	}

	@Test
	public void testAssertsInEachTest() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(pow);
		assertThat(testGenerator.testsFor(Results.class), everyItem(containsString("assert")));
	}

	@Test
	public void testCompilable() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(pow);
		assertThat(testGenerator.renderTest(Results.class), compiles());
	}
	
	@Test
	public void testRunnable() throws Exception {
		Results pow = new Results();
		for (int i = 1; i <= 10; i++) {
			pow.pow(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(pow);
		assertThat(testGenerator.renderTest(Results.class), testsRuns());
	}
	
}