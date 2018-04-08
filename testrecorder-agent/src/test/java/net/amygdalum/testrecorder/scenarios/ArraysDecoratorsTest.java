package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ArraysDecorators" })
public class ArraysDecoratorsTest {

	@Test
	public void testListsSetupCompilesAndRuns() throws Exception {
		ArraysDecorators dataTypes = new ArraysDecorators();

		dataTypes.consume(Arrays.asList("Hello", "World"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ArraysDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(ArraysDecorators.class)).satisfies(testsRun());
	}

	@Test
	public void testListsMatcherCompilesAndRuns() throws Exception {
		ArraysDecorators dataTypes = new ArraysDecorators();

		dataTypes.asList("Hello", "World");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ArraysDecorators.class)).hasSize(1);
		assertThat(testGenerator.renderTest(ArraysDecorators.class)).satisfies(testsRun());
	}

}