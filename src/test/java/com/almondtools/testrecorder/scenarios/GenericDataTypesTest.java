package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.SnapshotGenerator.setSnapshotConsumer;
import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.generator.TestGenerator;

public class GenericDataTypesTest {

	private static SnapshotInstrumentor instrumentor;

	private TestGenerator testGenerator;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.testrecorder.scenarios.GenericDataTypes");
	}
	
	@Before
	public void before() throws Exception {
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator);
	}

	@Test
	public void testCompilable() throws Exception {
		StringBuilder buffer = new StringBuilder();
		
		GenericDataTypes dataTypes = new GenericDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.objects(buffer, i);
		}
		assertThat(testGenerator.renderTest(GenericDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(GenericDataTypes.class), testsRuns());
	}
}