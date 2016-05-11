package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.GenericCollectionDataTypes"})
public class GenericCollectionDataTypesTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
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

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericCollectionDataTypes.class), hasSize(20));
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class), compiles(GenericCollectionDataTypes.class));
		assertThat(testGenerator.renderTest(GenericCollectionDataTypes.class), testsRun(GenericCollectionDataTypes.class));
	}
}