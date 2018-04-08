package net.amygdalum.testrecorder.scenarios;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Overridden", "net.amygdalum.testrecorder.scenarios.Overriding" })
public class OverrideTest {

	

	@Test
	public void testOverridingRecordedMethodsReplacingSuperDoesNotRecord() throws Exception {
		Overriding o = new Overriding();
		int result = o.methodForReplacement(0l);

		assertThat(result).isEqualTo(1);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Overridden.class)).isEmpty();
	}

	@Test
	public void testOverridingRecordedMethodsCallingSuperDoesNotRecord() throws Exception {
		Overriding o = new Overriding();
		int result = o.methodForExtension(0l);

		assertThat(result).isEqualTo(1);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Overridden.class)).isEmpty();
	}
}