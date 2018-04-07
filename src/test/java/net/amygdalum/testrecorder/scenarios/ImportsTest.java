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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Imports", "net.amygdalum.testrecorder.scenarios.Imports$List" })
public class ImportsTest {

	@Test
	public void testCompilable() throws Exception {
		Imports object = new Imports("name");

		assertThat(object.toString()).isEqualTo("[name]:name");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Imports.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		Imports object = new Imports("name");

		assertThat(object.toString()).isEqualTo("[name]:name");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Imports.class))
			.hasSize(1)
			.first().satisfies(test -> assertThat(test)
				.containsWildcardPattern("Imports imports? = new GenericObject() {*"
					+ "List<String> list = *"
					+ "net.amygdalum.testrecorder.scenarios.Imports.List otherList *"
					+ "}.as(Imports.class);")
				.containsWildcardPattern("new GenericMatcher() {*"
					+ "Matcher<?> list = containsInOrder(String.class, \"name\");*"
					+ "Matcher<?> otherList = new GenericMatcher() {*"
					+ "String name = \"name\";*"
					+ "}.matching(net.amygdalum.testrecorder.scenarios.Imports.List.class);*"
					+ "}.matching(Imports.class)"));
		assertThat(testGenerator.renderTest(Imports.class).getTestCode())
			.containsSequence("import java.util.List;")
			.doesNotContain("import net.amygdalum.testrecorder.scenarios.Imports.List;");
	}
}