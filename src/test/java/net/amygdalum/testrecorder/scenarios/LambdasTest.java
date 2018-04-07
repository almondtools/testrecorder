package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Lambdas" }, serializeLambdas = true)
public class LambdasTest {

	@Test
	public void testLambdaFieldCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Object result = lambdas.id(42);

		assertThat(result).isEqualTo(42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class)).satisfies(testsRun());
	}

	@Test
	public void testSerializableLambdaFieldCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Object result = lambdas.serializedId(42);

		assertThat(result).isEqualTo(42);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class)).satisfies(testsRun());
	}

	@Test
	public void testLambdaArgumentCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Object result = lambdas.exec(() -> 43);

		assertThat(result).isEqualTo(43);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class)).satisfies(testsRun());
	}

	@Test
	public void testLambdaResultCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Supplier<Object> result = lambdas.defer(44);

		assertThat(result.get()).isEqualTo(44);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class)).satisfies(testsRun());
	}
}