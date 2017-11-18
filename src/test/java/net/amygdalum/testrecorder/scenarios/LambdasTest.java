package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Lambdas" }, serializeLambdas = true)
public class LambdasTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testLambdaFieldCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Object result = lambdas.id(42);

		assertThat(result, equalTo(42));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class), compiles(Lambdas.class));
		assertThat(testGenerator.renderTest(Lambdas.class), testsRun(Lambdas.class));
	}

	@Test
	public void testSerializableLambdaFieldCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Object result = lambdas.serializedId(42);

		assertThat(result, equalTo(42));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class), compiles(Lambdas.class));
		assertThat(testGenerator.renderTest(Lambdas.class), testsRun(Lambdas.class));
	}

	@Test
	public void testLambdaArgumentCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Object result = lambdas.exec(() -> 43);

		assertThat(result, equalTo(43));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class), compiles(Lambdas.class));
		assertThat(testGenerator.renderTest(Lambdas.class), testsRun(Lambdas.class));
	}

	@Test
	public void testLambdaResultCompilable() throws Exception {
		Lambdas lambdas = new Lambdas();

		Supplier<Object> result = lambdas.defer(44);

		assertThat(result.get(), equalTo(44));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Lambdas.class), compiles(Lambdas.class));
		assertThat(testGenerator.renderTest(Lambdas.class), testsRun(Lambdas.class));
	}
}