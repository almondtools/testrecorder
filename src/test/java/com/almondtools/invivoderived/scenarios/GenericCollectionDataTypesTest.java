package com.almondtools.invivoderived.scenarios;

import static com.almondtools.invivoderived.SnapshotGenerator.setSnapshotConsumer;
import static com.almondtools.invivoderived.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.invivoderived.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.invivoderived.SnapshotInstrumentor;
import com.almondtools.invivoderived.generator.TestGenerator;

public class GenericCollectionDataTypesTest {

	private static SnapshotInstrumentor instrumentor;

	private TestGenerator testGenerator;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor();
		instrumentor.register("com.almondtools.invivoderived.scenarios.GenericCollectionDataTypes");
	}
	
	@Before
	public void before() throws Exception {
		testGenerator = new TestGenerator();
		setSnapshotConsumer(testGenerator);
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
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class), compiles());
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class), testsRuns());
	}
}