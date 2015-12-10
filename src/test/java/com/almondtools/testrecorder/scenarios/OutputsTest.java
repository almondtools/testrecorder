package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.almondtools.testrecorder.ConfigRegistry;
import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.TestGenerator;
import com.almondtools.testrecorder.util.Instrumented;
import com.almondtools.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"com.almondtools.testrecorder.scenarios.Outputs"})
public class OutputsTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}
	
	@Test
	public void testCompilableNotRecorded() throws Exception {
		Outputs out = new Outputs();
		out.notrecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.testsFor(Outputs.class), empty());
	}
	
	@Test
	public void testCompilable() throws Exception {
		Outputs out = new Outputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.renderTest(Outputs.class), compiles());
	}
	
	@Test
	public void testRunnable() throws Exception {
		Outputs out = new Outputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		System.out.println(testGenerator.renderTest(Outputs.class));
		assertThat(testGenerator.renderTest(Outputs.class), testsRuns());
	}
	
}