package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.PrimitiveDataTypes"})
public class PrimitiveDataTypesTest {

    @Before
    public void before() throws Exception {
        TestGenerator.fromRecorded().clearResults();
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

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), compiles(PrimitiveDataTypes.class));
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), testsRun(PrimitiveDataTypes.class));
    }
    
	@Test
	public void testAsserts() throws Exception {
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
		assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat(false, equalTo(false))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat(true, equalTo(true))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat('\u0001', equalTo('\u0001'))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat((byte) 1, equalTo((byte) 1))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat((short) 1, equalTo((short) 1))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat(1, equalTo(1))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat(1.0f, equalTo(1.0f))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat(1l, equalTo(1l))")));
        assertThat(testGenerator.renderTest(PrimitiveDataTypes.class), not(containsString("assertThat(1.0, equalTo(1.0))")));
	}
}