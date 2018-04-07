package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.LeafMethods", "net.amygdalum.testrecorder.scenarios.LeafType" })
public class LeafMethodsTest {

	@Test
	public void testCompilable() throws Exception {
		LeafMethods leafMethods = new LeafMethods();
		leafMethods.init(new LeafType(leafMethods));

		assertThat(leafMethods.method()).containsWildcardPattern("'net.amygdalum.testrecorder.scenarios.LeafMethods@*'");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(LeafType.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		LeafMethods leafMethods = new LeafMethods();
		leafMethods.init(new LeafType(leafMethods));

		assertThat(leafMethods.method()).containsWildcardPattern("'net.amygdalum.testrecorder.scenarios.LeafMethods@*'");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(LeafType.class)).hasSize(1);
	}

}