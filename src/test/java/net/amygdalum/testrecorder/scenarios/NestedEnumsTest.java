package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.NestedEnums" })
public class NestedEnumsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testNestedEnumsCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.inc(new NestedEnum("FIRST"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class), hasSize(1));
		System.out.println(testGenerator.testsFor(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), compiles(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), testsRun(NestedEnums.class));
	}

	@Test
	public void testNestedEnumsAsArgumentCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.name(new NestedEnum("FIRST").unwrap());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class), hasSize(1));
		System.out.println(testGenerator.testsFor(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), compiles(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), testsRun(NestedEnums.class));
	}

	@Test
	public void testNestedEnumsAsObjectArgumentCompilable() throws Exception {
		NestedEnums dataTypes = new NestedEnums();

		dataTypes.toString(new NestedEnum("FIRST").unwrap());

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(NestedEnums.class), hasSize(1));
		System.out.println(testGenerator.testsFor(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), compiles(NestedEnums.class));
		assertThat(testGenerator.renderTest(NestedEnums.class), testsRun(NestedEnums.class));
	}

}