package net.amygdalum.testrecorder.ioscenarios;

import static java.util.stream.Collectors.joining;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.Compiles.compiles;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static net.amygdalum.testrecorder.test.TestsRun.testsRunWith;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.test.Compiles;

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
			.containsWildcardPattern(".addVirtual(inputs?, \"Hello\")")
			.containsWildcardPattern(".addVirtual(inputs?, \" \")")
			.containsWildcardPattern(".addVirtual(inputs?, \"World\")");
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
				+ ".addVirtual(inputs?, \"Hello\")"
				+ ".addVirtual(inputs?, \"World\")");
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
				+ ".addVirtual(inputs?, true)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public byte readByte() {*}*})"
				+ ".addVirtual(inputs?, (byte) 42)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public short readShort() {*}*})"
				+ ".addVirtual(inputs?, (short) 42)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public int readInt() {*}*})"
				+ ".addVirtual(inputs?, 42)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public long readLong() {*}*})"
				+ ".addVirtual(inputs?, 42l)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public float readFloat() {*}*})"
				+ ".addVirtual(inputs?, 42.0f)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public double readDouble() {*}*})"
				+ ".addVirtual(inputs?, 42.0)")
			.containsWildcardPattern(".fakeInput(new Aspect() {*public char readChar() {*}*})"
				+ ".addVirtual(inputs?, 'a')");
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

	@Test
	public void testRobustOnSyntacticalChanges() throws Exception {
		Inputs in = new Inputs();
		in.recorded();
		String codeAfterJoiningAllLines = Files.lines(Paths.get("src/test/java/" + Inputs.class.getName().replace('.', '/') + ".java"))
			.collect(joining(" "));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(testsRunWith(codeAfterJoiningAllLines));
	}

	@Test
	public void testRobustOnSmallRefactoringsLikeExtractMethod() throws Exception {
		Inputs in = new Inputs();
		in.recorded();
		String codeAfterExtractingMethod = Files.lines(Paths.get("src/test/java/" + Inputs.class.getName().replace('.', '/') + ".java"))
			.collect(joining("\n"))
			.replace("public String recorded() {", ""
				+ "public String recorded() {"
				+ "  return delegated();"
				+ "}"
				+ "public String delegated() {");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Inputs.class)).satisfies(testsRunWith(codeAfterExtractingMethod));
	}

}