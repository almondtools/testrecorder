package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

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
		assertThat(testGenerator.renderTest(RejectedCases.class), compiles(RejectedCases.class));
	}

	@Test
	public void testRecorded() throws Exception {
		RejectedCases object = new RejectedCases();

		object.recorded();
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(RejectedCases.class)).hasSize(4);
		assertThat(testGenerator.renderTest(RejectedCases.class), compiles(RejectedCases.class));
		assertThat(testGenerator.renderTest(RejectedCases.class), testsRun(RejectedCases.class));
	}

}