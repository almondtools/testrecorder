package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.test.JUnit4TestsFail.testsFail;
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
	"java.lang.reflect.Array",
	"java.io.OutputStream",
	"java.io.FileOutputStream",
	"java.nio.channels.FileChannel",
	"java.io.ByteArrayOutputStream",
	"java.io.RandomAccessFile"}, config = StandardLibOutputTestRecorderAgentConfig.class)
public class StandardLibOutputTest {

	@Test
	public void testDirectNativeMethodCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.sleep();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testDirectNativeMethodWithResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		byte[] buffer = new byte[] {(byte) 42};

		byte extracted = io.extract(buffer);

		assertThat(extracted).isEqualTo((byte) 42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testNativeMethodCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		io.write("string");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testNativeMethodWithResultCompilesAndRuns(TestRecorderAgent agent) throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();

		byte[] result = io.readRandomAccessFile(new byte[] {41, 42});

		assertThat(result).isEqualTo(new byte[] {41, 42});

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class))
			.withFailMessage("tests should fail, because input method was declared as output")
			.satisfies(testsFail());
	}

	@Test
	public void testJavaMethodNoResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.store("My Output");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

	@Test
	public void testJavaMethodWithResultCompilesAndRuns() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		io.storeBuffered("My Output");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class).getTestCode()).containsSubsequence(
			"FakeIO",
			"fakeOutput");
		assertThat(testGenerator.renderTest(StandardLibInputOutput.class)).satisfies(testsRun());
	}

}
