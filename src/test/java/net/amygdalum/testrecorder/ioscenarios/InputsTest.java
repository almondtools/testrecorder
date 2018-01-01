package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.testing.assertj.Compiles.compiles;
import static net.amygdalum.testrecorder.testing.assertj.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.testing.assertj.Compiles;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.Inputs" })
public class InputsTest {

	@Test
	public void testCompilableNotRecorded() throws Exception {
		Inputs in = new Inputs();
		in.notrecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Inputs.class)).isEmpty();
	}

	@Test
	public void testCompilable() throws Exception {
		Inputs in = new Inputs();
		in.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(Compiles.compiles());
	}

	@Test
	public void testCompilableConditionalReturn() throws Exception {
		Inputs in = new Inputs();
		in.recordedWithConditionalReturns();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(compiles());
	}

	@Test
	public void testPrimitivesCompilable() throws Exception {
		Inputs in = new Inputs();
		in.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(compiles());
	}

	@Test
	public void testSideEffectsCompilable() throws Exception {
		Inputs in = new Inputs();
		in.sideEffectsRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(compiles());
	}

	@Test
	public void testObjectSideEffectsCompilable() throws Exception {
		Inputs in = new Inputs();
		in.objectSideEffectsRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(compiles());
	}

	@Test
	public void testRunnable() throws Exception {
		Inputs in = new Inputs();
		in.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class).getTestCode())
			.containsWildcardPattern("FakeIO.fake(Inputs.class)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public String read() {*}*})")
			.containsWildcardPattern(".add(Inputs.class, \"recorded\", *, \"Hello\")")
			.containsWildcardPattern(".add(Inputs.class, \"recorded\", *, \" \")")
			.containsWildcardPattern(".add(Inputs.class, \"recorded\", *, \"World\")");
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(testsRun());
	}

	@Test
	public void testRunnableConditionalReturns() throws Exception {
		Inputs in = new Inputs();
		in.recordedWithConditionalReturns();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class).getTestCode())
			.containsWildcardPattern("FakeIO.fake(Inputs.class)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public String conditionalReturnRead() {*}*})"
				+ ".add(Inputs.class, \"recordedWithConditionalReturns\", *, \"Hello\")"
				+ ".add(Inputs.class, \"recordedWithConditionalReturns\", *, \"World\")");
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(testsRun());
	}

	@Test
	public void testPrimitivesRunnable() throws Exception {
		Inputs in = new Inputs();
		in.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class).getTestCode())
			.containsWildcardPattern("FakeIO.fake(Inputs.class)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public boolean readBoolean() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, true)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public byte readByte() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, (byte) 42)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public short readShort() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, (short) 42)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public int readInt() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, 42)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public long readLong() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, 42l)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public float readFloat() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, 42.0f)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public double readDouble() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, 42.0)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public char readChar() {*}*})"
				+ ".add(Inputs.class, \"primitivesRecorded\", *, 'a')");
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(testsRun());
	}

	@Test
	public void testSideEffectsRunnable() throws Exception {
		Inputs in = new Inputs();
		in.sideEffectsRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(testsRun());
	}

	@Test
	public void testObjectSideEffectsRunnable() throws Exception {
		Inputs in = new Inputs();
		in.objectSideEffectsRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(testsRun());
	}

}