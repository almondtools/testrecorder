package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.SharedState", "net.amygdalum.testrecorder.scenarios.State" })
public class SharedStateTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		State state = new State();
		SharedState shared1 = SharedState.create(state);
		SharedState shared2 = SharedState.create(state);
		
		String result = shared1.combine(shared2);
		
		assertThat(result, equalTo(":."));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SharedState.class), compiles(SharedState.class));
		assertThat(testGenerator.renderTest(SharedState.class), TestsRunnableMatcher.testsRun(SharedState.class));
	}

}