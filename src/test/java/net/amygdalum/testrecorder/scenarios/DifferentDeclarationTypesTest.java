package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={
	"net.amygdalum.testrecorder.scenarios.DifferentDeclarationTypes",
	"net.amygdalum.testrecorder.scenarios.MyEnum",
	"net.amygdalum.testrecorder.scenarios.MyAnnotation",
	"net.amygdalum.testrecorder.scenarios.MyInterface",
	"net.amygdalum.testrecorder.scenarios.MyClass"})
public class DifferentDeclarationTypesTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		DifferentDeclarationTypes types = new DifferentDeclarationTypes();
		
		types.test();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DifferentDeclarationTypes.class), compiles());
		assertThat(testGenerator.renderTest(DifferentDeclarationTypes.class), testsRuns());
	}

}