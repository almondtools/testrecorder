package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.ioscenarios.NestedInput"})
public class NestedInputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		NestedInput input = new NestedInput();
		String time = input.getTime();

		assertThat(time.matches("\\d+:\\d+"), is(true));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedInput.class), compiles(NestedInput.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
		NestedInput input = new NestedInput();
		String time = input.getTime();

		assertThat(time.matches("\\d+:\\d+"), is(true));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedInput.class), testsRun(NestedInput.class));
	}
	
}
