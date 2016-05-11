package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.SideEffects"})
public class SideEffectsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testSideEffectsOnThis() throws Exception {
		SideEffects sideEffects = new SideEffects();
		for (int i = 0; i < 100; i += sideEffects.getI()) {
			sideEffects.methodWithSideEffectOnThis(i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(SideEffects.class), hasSize(7));
		assertThat(testGenerator.testsFor(SideEffects.class), everyItem(containsString("assert")));
	}

	@Test
	public void testSideEffectsOnArgument() throws Exception {
		int[] array = new int[] { 0 };
		SideEffects sideEffects = new SideEffects();
		for (int i = 0; i < 10; i++) {
			sideEffects.methodWithSideEffectOnArgument(array);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(SideEffects.class), hasSize(10));
		assertThat(testGenerator.testsFor(SideEffects.class), everyItem(containsString("assert")));
	}

	@Test
	public void testSideEffectsCompilable() throws Exception {
		int[] array = new int[] { 0 };
		SideEffects sideEffects = new SideEffects();
		for (int i = 0; i < 100; i += sideEffects.getI()) {
			sideEffects.methodWithSideEffectOnThis(i);
		}
		for (int i = 0; i < 10; i++) {
			sideEffects.methodWithSideEffectOnArgument(array);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SideEffects.class), compiles(SideEffects.class));
		assertThat(testGenerator.renderTest(SideEffects.class), testsRun(SideEffects.class));
	}

}