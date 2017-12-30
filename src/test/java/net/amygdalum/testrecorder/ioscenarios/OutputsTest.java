package net.amygdalum.testrecorder.ioscenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.ioscenarios.Outputs" })
public class OutputsTest {

	

	@Test
	public void testCompilableNotRecorded() throws Exception {
		Outputs out = new Outputs();
		out.notrecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Outputs.class), empty());
	}

	@Test
	public void testCompilable() throws Exception {
		Outputs out = new Outputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class), compiles(Outputs.class));
	}

	@Test
	public void testCompilableConditionalReturn() throws Exception {
		Outputs out = new Outputs();
		out.recordedWithConditionalReturn();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class), compiles(Outputs.class));
	}

	@Test
	public void testPrimitivesCompilable() throws Exception {
		Outputs out = new Outputs();
		out.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class), compiles(Outputs.class));
	}

	@Test
	public void testRunnable() throws Exception {
		Outputs out = new Outputs();
		out.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Outputs.class), hasSize(1));
		assertThat(testGenerator.renderTest(Outputs.class), containsPattern(".fakeOutput(new Aspect() {*print(String*)*})"
			+ ".add(Outputs.class, \"recorded\", *, null, equalTo(\"Hello \"))"
			+ ".add(Outputs.class, \"recorded\", *, null, equalTo(\"World\")"));
		assertThat(testGenerator.renderTest(Outputs.class), containsString("verify()"));
		assertThat(testGenerator.renderTest(Outputs.class), testsRun(Outputs.class));
	}

	@Test
	public void testRunnableConditionalReturn() throws Exception {
		Outputs out = new Outputs();
		out.recordedWithConditionalReturn();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Outputs.class), hasSize(1));
		assertThat(testGenerator.renderTest(Outputs.class), containsPattern(".fakeOutput(new Aspect() {*conditionalReturnOutput(char*)*})"
			+ ".add(Outputs.class, \"recordedWithConditionalReturn\", *, true, equalTo('a'))"
			+ ".add(Outputs.class, \"recordedWithConditionalReturn\", *, true, equalTo(','))"
			+ ".add(Outputs.class, \"recordedWithConditionalReturn\", *, false, equalTo(' '))"
			+ ".add(Outputs.class, \"recordedWithConditionalReturn\", *, true, equalTo('b'))"
			+ ".add(Outputs.class, \"recordedWithConditionalReturn\", *, false, equalTo('\\n')"));

		assertThat(testGenerator.renderTest(Outputs.class), containsString("verify()"));
		assertThat(testGenerator.renderTest(Outputs.class), testsRun(Outputs.class));
	}

	@Test
	public void testPrimitivesRunnable() throws Exception {
		Outputs out = new Outputs();
		out.primitivesRecorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Outputs.class), containsString("verify()"));
		assertThat(testGenerator.renderTest(Outputs.class), testsRun(Outputs.class));
	}

}