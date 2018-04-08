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

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.Outputs" })
public class OutputsTest {

	@Test
	public void testCompilableNotRecorded() throws Exception {
		Outputs out = new Outputs();
		out.notrecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Outputs.class)).isEmpty();
	}

	@Test
	public void testCompilable() throws Exception {
		Outputs out = new Outputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(compiles());
	}

	@Test
	public void testCompilableConditionalReturn() throws Exception {
		Outputs out = new Outputs();
		out.recordedWithConditionalReturn();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(compiles());
	}

	@Test
	public void testPrimitivesCompilable() throws Exception {
		Outputs out = new Outputs();
		out.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(compiles());
	}

	@Test
	public void testRunnable() throws Exception {
		Outputs out = new Outputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Outputs.class)).hasSize(1);
		assertThat(testGenerator.renderTest(Outputs.class).getTestCode())
			.containsWildcardPattern(".fakeOutput(new Aspect() {*print(String*)*})"
				+ ".addVirtual(outputs?, null, equalTo(\"Hello \"))"
				+ ".addVirtual(outputs?, null, equalTo(\"World\")")
			.contains("verify()");
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(testsRun());
	}

	@Test
	public void testRunnableConditionalReturn() throws Exception {
		Outputs out = new Outputs();
		out.recordedWithConditionalReturn();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Outputs.class)).hasSize(1);
		assertThat(testGenerator.renderTest(Outputs.class).getTestCode())
			.containsWildcardPattern(".fakeOutput(new Aspect() {*conditionalReturnOutput(char*)*})"
				+ ".addVirtual(outputs?, true, equalTo('a'))"
				+ ".addVirtual(outputs?, true, equalTo(','))"
				+ ".addVirtual(outputs?, false, equalTo(' '))"
				+ ".addVirtual(outputs?, true, equalTo('b'))"
				+ ".addVirtual(outputs?, false, equalTo('\\n')")
			.contains("verify()");
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(testsRun());
	}

	@Test
	public void testPrimitivesRunnable() throws Exception {
		Outputs out = new Outputs();
		out.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class).getTestCode()).contains("verify()");
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(testsRun());
	}

	@Test
	public void testRobustOnSyntacticalChanges() throws Exception {
		Outputs out = new Outputs();
		out.recorded();
		String codeAfterJoiningAllLines = Files.lines(Paths.get("src/test/java/" + Outputs.class.getName().replace('.', '/') + ".java"))
			.collect(joining(" "));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(testsRunWith(codeAfterJoiningAllLines));
	}

	@Test
	public void testRobustOnSmallRefactoringsLikeExtractMethod() throws Exception {
		Outputs out = new Outputs();
		out.recorded();
		String codeAfterExtractingMethod = Files.lines(Paths.get("src/test/java/" + Outputs.class.getName().replace('.', '/') + ".java"))
			.collect(joining("\n"))
			.replace("public void recorded() {", ""
				+ "public void recorded() {"
				+ "  delegated();"
				+ "}"
				+ "public void delegated() {");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class)).satisfies(testsRunWith(codeAfterExtractingMethod));
	}


}