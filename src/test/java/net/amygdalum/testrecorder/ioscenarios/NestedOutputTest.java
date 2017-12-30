package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes={"net.amygdalum.testrecorder.ioscenarios.NestedOutput"})
public class NestedOutputTest {

	
	
	@Test
	public void testCompilable() throws Exception {
		NestedOutput input = new NestedOutput();
		int time = input.getTime();
		time = input.getTime();

		assertThat(time, equalTo(2));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedOutput.class), compiles(NestedOutput.class));
	}
	
	@Test
	public void testRunnable() throws Exception {
		NestedOutput input = new NestedOutput();
		int time = input.getTime();
		time = input.getTime();

		assertThat(time, equalTo(2));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(NestedOutput.class), testsRun(NestedOutput.class));
	}
	
}
