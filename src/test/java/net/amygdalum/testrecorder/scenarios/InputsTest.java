package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

import net.amygdalum.testrecorder.ConfigRegistry;
import net.amygdalum.testrecorder.DefaultConfig;
import net.amygdalum.testrecorder.TestGenerator;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.Inputs"})
public class InputsTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}
	
	@Test
	public void testCompilableNotRecorded() throws Exception {
		Inputs out = new Inputs();
		out.notrecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.testsFor(Inputs.class), empty());
	}
	
	@Test
	public void testCompilable() throws Exception {
		Inputs out = new Inputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.renderTest(Inputs.class), compiles());
	}
	
	@Test
	public void testPrimitivesCompilable() throws Exception {
		Inputs out = new Inputs();
		out.primitivesRecorded();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.renderTest(Inputs.class), compiles());
	}
	
	@Test
	public void testSideEffectsCompilable() throws Exception {
		Inputs out = new Inputs();
		out.sideEffectsRecorded();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.renderTest(Inputs.class), compiles());
	}
	
	@Test
	public void testRunnable() throws Exception {
		Inputs out = new Inputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.renderTest(Inputs.class), testsRuns());
	}
	
	@Test
	public void testPrimitivesRunnable() throws Exception {
		Inputs out = new Inputs();
		out.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.renderTest(Inputs.class), testsRuns());
	}
	
	@Test
	public void testSideEffectsRunnable() throws Exception {
		Inputs out = new Inputs();
		out.sideEffectsRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded(out);
		assertThat(testGenerator.renderTest(Inputs.class), testsRuns());
	}
	
}