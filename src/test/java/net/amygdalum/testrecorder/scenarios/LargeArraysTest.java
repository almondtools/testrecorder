package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.LargeIntArrays", "net.amygdalum.testrecorder.scenarios.LargeObjectArrays" })
public class LargeArraysTest {

	@Rule
	public Timeout globalTimeout= new Timeout(30, TimeUnit.SECONDS);
	
	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testLargeIntArraysArgumentCompilable() throws Exception {
		LargeIntArrays arrays = new LargeIntArrays();

		arrays.initInts(400);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeIntArrays.class), hasSize(1));
		assertThat(testGenerator.renderTest(LargeIntArrays.class), compiles(LargeIntArrays.class));
		assertThat(testGenerator.renderTest(LargeIntArrays.class), testsRun(LargeIntArrays.class));
	}

	@Test
	public void testLargeIntArraysClassCompilable() throws Exception {
		LargeIntArrays arrays = new LargeIntArrays(100);

		arrays.sum();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeIntArrays.class), hasSize(2));
		assertThat(testGenerator.renderTest(LargeIntArrays.class), compiles(LargeIntArrays.class));
		assertThat(testGenerator.renderTest(LargeIntArrays.class), testsRun(LargeIntArrays.class));
	}

	@Test
	public void testLargeObjectArraysArgumentCompilable() throws Exception {
		LargeObjectArrays arrays = new LargeObjectArrays();

		arrays.initObjects(100);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeObjectArrays.class), hasSize(1));
		assertThat(testGenerator.renderTest(LargeObjectArrays.class), compiles(LargeObjectArrays.class));
		assertThat(testGenerator.renderTest(LargeObjectArrays.class), testsRun(LargeObjectArrays.class));
	}

	@Test 
	public void testLargeObjectArraysClassCompilable() throws Exception {
		LargeObjectArrays arrays = new LargeObjectArrays(100);

		arrays.sum();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeObjectArrays.class), hasSize(2));
		assertThat(testGenerator.renderTest(LargeObjectArrays.class), compiles(LargeObjectArrays.class));
		assertThat(testGenerator.renderTest(LargeObjectArrays.class), testsRun(LargeObjectArrays.class));
	}
}