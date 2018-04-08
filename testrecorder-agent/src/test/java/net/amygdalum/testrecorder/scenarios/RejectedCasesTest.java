package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.Compiles.compiles;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {
	"net.amygdalum.testrecorder.scenarios.RejectedCases",
	"net.amygdalum.testrecorder.scenarios.RejectedCases$ProtectedObject",
	"net.amygdalum.testrecorder.scenarios.RejectedCases$PackagePrivateObject",
	"net.amygdalum.testrecorder.scenarios.RejectedCases$PrivateObject"
})
public class RejectedCasesTest {

	@Test
	public void testRejectedEmpty() throws Exception {
		RejectedCases object = new RejectedCases();

		object.rejected();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(RejectedCases.class)).isEmpty();
		assertThat(testGenerator.renderTest(RejectedCases.class)).satisfies(compiles());
	}

	@Test
	public void testRecorded() throws Exception {
		RejectedCases object = new RejectedCases();

		object.recorded();

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(RejectedCases.class)).hasSize(4);
		assertThat(testGenerator.renderTest(RejectedCases.class)).satisfies(testsRun());
	}

}