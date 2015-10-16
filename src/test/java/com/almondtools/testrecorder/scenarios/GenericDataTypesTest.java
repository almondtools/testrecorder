package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.generator.TestGenerator;

public class GenericDataTypesTest {

	private static SnapshotInstrumentor instrumentor;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor(new DefaultConfig());
		instrumentor.register("com.almondtools.testrecorder.scenarios.GenericDataTypes");
	}
	
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