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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.HiddenInnerClass" })
public class HiddenInnerClassTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");

		assertThat(object.toString()).isEqualTo("hidden name");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(HiddenInnerClass.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");

		assertThat(object.toString()).isEqualTo("hidden name");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(HiddenInnerClass.class))
			.hasSize(1)
			.first().satisfies(test -> assertThat(test)
				.containsWildcardPattern("Object object? = *new GenericObject() {*"
					+ "String name = \"hidden name\";*"
					+ "}.as(clazz(\"net.amygdalum.testrecorder.scenarios.HiddenInnerClass$Hidden\")).value();")
				.containsWildcardPattern("HiddenInnerClass hiddenInnerClass? = new GenericObject() {*Object o = object?;*}.as(HiddenInnerClass.class)")
				.containsWildcardPattern("new GenericMatcher() {*"
					+ "Matcher<?> o = new GenericMatcher() {*"
					+ "String name = \"hidden name\";*"
					+ "}.matching(clazz(\"net.amygdalum.testrecorder.scenarios.HiddenInnerClass$Hidden\"), Object.class);*"
					+ "}"
					+ ".matching(HiddenInnerClass.class));"));
	}
}