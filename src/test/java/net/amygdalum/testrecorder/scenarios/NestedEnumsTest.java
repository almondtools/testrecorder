package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.NestedEnums" })
public class NestedEnumsTest {

	@Test
	public void testNestedEnumsCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.inc(new NestedEnum("FIRST"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class)).hasSize(1);
		assertThat(testGenerator.renderTest(NestedEnums.class)).satisfies(testsRun());
	}

	@Test
	public void testNestedEnumsAsArgumentCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.name(new NestedEnum("FIRST").unwrap());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class)).hasSize(1);
		assertThat(testGenerator.renderTest(NestedEnums.class)).satisfies(testsRun());
	}

	@Test
	public void testNestedEnumsAsObjectArgumentCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.toString(new NestedEnum("FIRST").unwrap());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class)).hasSize(1);
		assertThat(testGenerator.renderTest(NestedEnums.class)).satisfies(testsRun());
	}

}