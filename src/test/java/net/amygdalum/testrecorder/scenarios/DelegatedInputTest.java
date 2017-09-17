package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Debug;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.DelegatedInput"})
public class DelegatedInputTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		DelegatedInput.SingleInput singleInput = new DelegatedInput.SingleInput();
		DelegatedInput input1 = new DelegatedInput(singleInput);
		DelegatedInput input2 = new DelegatedInput(singleInput);
		input1.combine(input2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DelegatedInput.class), compiles(DelegatedInput.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
		DelegatedInput.SingleInput singleInput = new DelegatedInput.SingleInput();
		DelegatedInput input1 = new DelegatedInput(singleInput);
		DelegatedInput input2 = new DelegatedInput(singleInput);
		input1.combine(input2);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(Debug.print(testGenerator.renderTest(DelegatedInput.class)), testsRun(DelegatedInput.class));
	}
	
}
