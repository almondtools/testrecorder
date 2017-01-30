package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.LargeArrays" })
public class LargeArraysTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testLargeArraysArgumentCompilable() throws Exception {
		LargeArrays arrays = new LargeArrays();

		arrays.initInts(400);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeArrays.class), hasSize(1));
		assertThat(testGenerator.renderTest(LargeArrays.class), compiles(LargeArrays.class));
		assertThat(testGenerator.renderTest(LargeArrays.class), testsRun(LargeArrays.class));
	}

	@Test
	public void testLargeArraysClassCompilable() throws Exception {
		LargeArrays arrays = new LargeArrays(100);

		arrays.sum();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeArrays.class), hasSize(2));
		assertThat(testGenerator.renderTest(LargeArrays.class), compiles(LargeArrays.class));
		assertThat(testGenerator.renderTest(LargeArrays.class), testsRun(LargeArrays.class));
	}

}