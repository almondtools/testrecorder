package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ObjectCycles" })
public class ObjectCyclesTest {

	@Test
	public void testCompilable() throws Exception {
		ObjectCycles a = new ObjectCycles();
		ObjectCycles b = new ObjectCycles();
		a.next(b);
		b.next(a);

		assertThat(a.getNext()).isSameAs(b);
		assertThat(a.getPrev()).isSameAs(b);
		assertThat(b.getNext()).isSameAs(a);
		assertThat(b.getPrev()).isSameAs(a);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ObjectCycles.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		ObjectCycles a = new ObjectCycles();
		ObjectCycles b = new ObjectCycles();
		a.next(b);
		b.next(a);

		assertThat(a.getNext()).isSameAs(b);
		assertThat(a.getPrev()).isSameAs(b);
		assertThat(b.getNext()).isSameAs(a);
		assertThat(b.getPrev()).isSameAs(a);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ObjectCycles.class).getTestCode())
			.containsPattern(""
				+ "ObjectCycles objectCycles\\d = new GenericObject\\(\\) \\{\\s*"
				+ "ObjectCycles next = objectCycles\\d;\\s*"
				+ "ObjectCycles prev = objectCycles\\d;\\s*"
				+ "\\}");
	}
}