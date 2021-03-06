package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.LargeIntArrays" })
public class LargeArraysTest {

	@Test
	public void testLargeIntArraysResultAndArgumentCode() throws Exception {
		LargeIntArrays arrays = new LargeIntArrays();

		int[][] result = arrays.initInts(400);
		arrays.doubleInts(result);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(LargeIntArrays.class).getTestCode())
			.doesNotContainWildcardPattern("int[][] intArrayArray? = new int[][]{intArray1, intArray2");
	}

	@Test
	public void testLargeIntArraysResultAndArgumentCompilable() throws Exception {
		LargeIntArrays arrays = new LargeIntArrays();

		int[][] result = arrays.initInts(400);
		arrays.doubleInts(result);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeIntArrays.class)).hasSize(2);
		assertThat(testGenerator.renderTest(LargeIntArrays.class)).satisfies(testsRun());
	}

	@Test
	public void testLargeIntArraysClassCompilable() throws Exception {
		LargeIntArrays arrays = new LargeIntArrays(100);

		arrays.sum();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LargeIntArrays.class)).hasSize(2);
		assertThat(testGenerator.renderTest(LargeIntArrays.class)).satisfies(testsRun());
	}

}