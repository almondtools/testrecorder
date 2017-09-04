package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.SystemInput"}, config=SystemInputTestRecorderAgentConfig.class)
public class SystemInputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		SystemInput time = new SystemInput();
		time.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SystemInput.class), compiles(SystemInput.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
        SystemInput time = new SystemInput();
        time.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		System.out.println(testGenerator.renderTest(SystemInput.class));
		assertThat(testGenerator.renderTest(SystemInput.class), testsRun(SystemInput.class));
	}
	
}
