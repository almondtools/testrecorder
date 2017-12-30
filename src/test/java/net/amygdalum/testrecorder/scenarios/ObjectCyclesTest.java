package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ObjectCycles" })
public class ObjectCyclesTest {

	

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