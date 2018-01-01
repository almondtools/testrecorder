package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.SharedState", "net.amygdalum.testrecorder.scenarios.StringState", "net.amygdalum.testrecorder.scenarios.State" })
public class SharedStateTest {

	@Test
	public void testCompilable() throws Exception {
		State state = new StringState();
		SharedState shared1 = SharedState.create(state);
		SharedState shared2 = SharedState.create(state);

		String result = shared1.combine(shared2);

		assertThat(result).isEqualTo(":.");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SharedState.class), compiles(SharedState.class));
		assertThat(testGenerator.renderTest(SharedState.class), testsRun(SharedState.class));
	}

	@Test
	public void testDeepSharingCompilable() throws Exception {
		StringState state = new StringState();
		SharedState shared1 = SharedState.create(SharedState.create(state));
		SharedState shared2 = SharedState.create(SharedState.create(state));

		String result = shared1.combine(shared2);

		assertThat(result).isEqualTo(":.");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SharedState.class), compiles(SharedState.class));
		assertThat(testGenerator.renderTest(SharedState.class), testsRun(SharedState.class));
	}

	@Test
	public void testNotSharedCompilable() throws Exception {
		SharedState notShared1 = SharedState.create(StringState.create(new String("")));
		SharedState notShared2 = SharedState.create(StringState.create(new String("")));

		String result = notShared1.combine(notShared2);

		assertThat(result).isEqualTo(":");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SharedState.class), compiles(SharedState.class));
		assertThat(testGenerator.renderTest(SharedState.class), testsRun(SharedState.class));
		assertThat(testGenerator.renderTest(SharedState.class)).containsWildcardPattern("SharedState sharedState* = new SharedState()*SharedState sharedState* = new SharedState()");
		assertThat(testGenerator.renderTest(SharedState.class), not(containsString("State stringState")));
	}

}