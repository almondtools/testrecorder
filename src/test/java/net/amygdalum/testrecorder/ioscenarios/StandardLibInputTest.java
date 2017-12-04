package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput", "java.lang.System"}, config=StandardLibInputOutputTestRecorderAgentConfig.class)
public class StandardLibInputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		StandardLibInputOutput time = new StandardLibInputOutput();
		time.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), allOf(
			containsString("FakeIO"), 
			containsString("fakeInput")));
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), compiles(StandardLibInputOutput.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
		StandardLibInputOutput time = new StandardLibInputOutput();
		time.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class), testsRun(StandardLibInputOutput.class));
	}
	
}
