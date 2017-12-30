package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ArraysDecorators" })
public class ArraysDecoratorsTest {

	

	@Test
	public void testListsSetupCompilable() throws Exception {
		ArraysDecorators dataTypes = new ArraysDecorators();

		dataTypes.consume(Arrays.asList("Hello", "World"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ArraysDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(ArraysDecorators.class), compiles(ArraysDecorators.class));
		assertThat(testGenerator.renderTest(ArraysDecorators.class), testsRun(ArraysDecorators.class));
	}

	@Test
	public void testListsMatcherCompilable() throws Exception {
		ArraysDecorators dataTypes = new ArraysDecorators();

		dataTypes.asList("Hello", "World");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ArraysDecorators.class), hasSize(1));
		assertThat(testGenerator.renderTest(ArraysDecorators.class), compiles(ArraysDecorators.class));
		assertThat(testGenerator.renderTest(ArraysDecorators.class), testsRun(ArraysDecorators.class));
	}

}