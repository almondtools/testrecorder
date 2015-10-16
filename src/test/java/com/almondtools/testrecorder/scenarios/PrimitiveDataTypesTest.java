package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.generator.TestGenerator;

public class PrimitiveDataTypesTest {

	private static SnapshotInstrumentor instrumentor;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor(new DefaultConfig());
		instrumentor.register("com.almondtools.testrecorder.scenarios.PrimitiveDataTypes");
	}
	
	@Test
	public void testCompilable() throws Exception {
		PrimitiveDataTypes dataTypes = new PrimitiveDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.booleans(i % 2 == 0);
			dataTypes.chars((char) i);
			dataTypes.bytes((byte) i);
			dataTypes.shorts((short) i);
			dataTypes.integers(i);
			dataTypes.floats((float) i);
			dataTypes.longs(i);
			dataTypes.doubles((double) i);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), testsRuns());
	}
}