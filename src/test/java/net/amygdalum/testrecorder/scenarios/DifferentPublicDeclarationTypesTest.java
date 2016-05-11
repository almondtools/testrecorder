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
@Instrumented(classes={
	"net.amygdalum.testrecorder.scenarios.DifferentPublicDeclarationTypes",
	"net.amygdalum.testrecorder.scenarios.DifferentPublicDeclarationTypes.MyEnum",
	"net.amygdalum.testrecorder.scenarios.DifferentPublicDeclarationTypes.MyAnnotation",
	"net.amygdalum.testrecorder.scenarios.DifferentPublicDeclarationTypes.MyInterface",
	"net.amygdalum.testrecorder.scenarios.DifferentPublicDeclarationTypes.MyClass"})
public class DifferentPublicDeclarationTypesTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		DifferentPublicDeclarationTypes types = new DifferentPublicDeclarationTypes();
		
		types.test();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DifferentPublicDeclarationTypes.class), compiles(DifferentPublicDeclarationTypes.class));
		assertThat(testGenerator.renderTest(DifferentPublicDeclarationTypes.class), testsRun(DifferentPublicDeclarationTypes.class));
	}

}