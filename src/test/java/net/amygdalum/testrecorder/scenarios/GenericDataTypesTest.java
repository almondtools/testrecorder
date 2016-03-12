package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

import net.amygdalum.testrecorder.TestGenerator;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.GenericDataTypes"})
public class GenericDataTypesTest {
	
	@Test
	public void testCompilable() throws Exception {
		StringBuilder buffer = new StringBuilder();
		
		GenericDataTypes dataTypes = new GenericDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.objects(buffer, i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.renderTest(GenericDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(GenericDataTypes.class), testsRuns());
	}
}