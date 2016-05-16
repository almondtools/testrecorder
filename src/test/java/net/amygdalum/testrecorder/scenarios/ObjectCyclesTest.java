package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ObjectCycles" })
public class ObjectCyclesTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		ObjectCycles a = new ObjectCycles();
		ObjectCycles b = new ObjectCycles();
		a.next(b);
		b.next(a);

		assertThat(a.getNext(), sameInstance(b));
		assertThat(a.getPrev(), sameInstance(b));
		assertThat(b.getNext(), sameInstance(a));
		assertThat(b.getPrev(), sameInstance(a));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ObjectCycles.class), compiles(ObjectCycles.class));
		assertThat(testGenerator.renderTest(ObjectCycles.class), testsRun(ObjectCycles.class));
	}
}