package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.generator.TestGenerator;

public class GenericCollectionDataTypesTest {

	private static SnapshotInstrumentor instrumentor;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor(new DefaultConfig());
		instrumentor.register("com.almondtools.testrecorder.scenarios.GenericCollectionDataTypes");
	}

	@Test
	public void testCompilable() throws Exception {
		List<BigInteger> bigInts = new ArrayList<>();
		List<BigDecimal> bigDecs = new ArrayList<>();
		
		GenericCollectionDataTypes dataTypes = new GenericCollectionDataTypes();
		for (int i = 1; i <= 10; i++) {
			dataTypes.bigIntegerLists(bigInts);
			dataTypes.bigDecimalLists(bigDecs);
		}

		TestGenerator testGenerator = TestGenerator.fromRecorded(dataTypes);
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class), testsRuns());
	}
}