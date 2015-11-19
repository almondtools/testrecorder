package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.almondtools.testrecorder.TestGenerator;
import com.almondtools.testrecorder.util.Instrumented;
import com.almondtools.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"com.almondtools.testrecorder.scenarios.GenericCollectionDataTypes"})
public class GenericCollectionDataTypesTest {

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