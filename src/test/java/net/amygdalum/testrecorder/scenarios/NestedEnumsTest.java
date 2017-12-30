package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.NestedEnums" })
public class NestedEnumsTest {

	

	@Test
	public void testNestedEnumsCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.inc(new NestedEnum("FIRST"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class), hasSize(1));
		assertThat(testGenerator.renderTest(NestedEnums.class), compiles(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), testsRun(NestedEnums.class));
	}

	@Test
	public void testNestedEnumsAsArgumentCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.name(new NestedEnum("FIRST").unwrap());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class), hasSize(1));
		assertThat(testGenerator.renderTest(NestedEnums.class), compiles(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), testsRun(NestedEnums.class));
	}

	@Test
	public void testNestedEnumsAsObjectArgumentCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.toString(new NestedEnum("FIRST").unwrap());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class), hasSize(1));
		assertThat(testGenerator.renderTest(NestedEnums.class), compiles(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), testsRun(NestedEnums.class));
	}

}