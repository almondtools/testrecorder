package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestRecorderAgent;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {
	"net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput",
	"java.lang.System",
	"java.lang.reflect.Array",
	"java.io.FileInputStream",
	"java.io.ObjectInputStream",
	"java.io.ObjectInputStream$BlockDataInputStream",
	"java.io.RandomAccessFile"}, config = StandardLibInputTestRecorderAgentConfig.class)
public class StandardLibInputTest {

	@Test
	public void testDirectNativeMethodCompilesAndRuns(TestRecorderAgent agent) throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		byte[] buffer = new byte[1];
		byte[] filled = io.fill(buffer, (byte) 42);

		assertThat(filled[0]).isEqualTo((byte) 42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		agent.withoutInstrumentation(() -> {
			assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
		});
	}

	@Test
	public void testDirectNativeMethodWithResultCompilesAndRuns(TestRecorderAgent agent) throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.getTimestamp();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		agent.withoutInstrumentation(() -> {
			assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
		});
	}

	@Test
	public void testNativeMethodCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		float[] result = io.serializeRoundtrip(new float[] {0, 1.0f});

		assertThat(result).containsExactly(0, 1.0f);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testNativeMethodWithResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		byte[] result = io.readRandomAccessFile(new byte[] {41, 42});

		assertThat(result).isEqualTo(new byte[] {41, 42});

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testJavaMethodWithResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		int result = io.readFile(new byte[] {41, 42}, 1);

		assertThat(result).isEqualTo(42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testJavaMethodWithArgsAndResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		byte[] result = io.readFile(new byte[] {41, 42});

		assertThat(result).isEqualTo(new byte[] {41, 42});

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeInput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

}
