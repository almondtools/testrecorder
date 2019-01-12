package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {"net.amygdalum.testrecorder.scenarios.This"})
public class ThisTest {

	@Test
	public void testReportingOfNoChanges() throws Exception {
		This args = new This("content");

		String result = args.getContent();

		assertThat(result).isEqualTo("content");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(This.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(This.class).getTestCode())
			.containsWildcardPattern("assertThat(\"expected no change, but was:\", this?, new GenericMatcher() {*String content = \"content\";*}.matching(This.class));");
	}
	
	@Test
	public void testReportingOfChanges() throws Exception {
		This args = new This("content");

		args.setContent("new");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(This.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(This.class).getTestCode())
			.containsWildcardPattern("assertThat(\"expected change:\", this?, new GenericMatcher() {*String content = \"new\";*}.matching(This.class));");
	}

}