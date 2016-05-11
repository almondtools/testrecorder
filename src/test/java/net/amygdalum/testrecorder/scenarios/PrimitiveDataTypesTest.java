package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.PrimitiveDataTypes"})
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

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), compiles(PrimitiveDataTypes.class));
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), testsRun(PrimitiveDataTypes.class));
	}
}