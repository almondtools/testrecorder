package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.almondtools.testrecorder.TestGenerator;
import com.almondtools.testrecorder.util.Instrumented;
import com.almondtools.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"com.almondtools.testrecorder.scenarios.PrimitiveDataTypes"})
public class PrimitiveDataTypesTest {

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