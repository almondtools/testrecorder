package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Exceptions" })
public class ExceptionsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		Exceptions out = new Exceptions();
		try {
			out.throwingException();
		} catch (IllegalArgumentException e) {
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Exceptions.class), compiles(Exceptions.class));
	}

	@Test
	public void testRunnable() throws Exception {
		Exceptions out = new Exceptions();
		try {
			out.throwingException();
		} catch (IllegalArgumentException e) {
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Exceptions.class), testsRun(Exceptions.class));
	}

}