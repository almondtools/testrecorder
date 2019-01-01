package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.DerivedMap" })
public class InheritanceHiddenTypesTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		DerivedMap<String, String> derived = new DerivedMap<>();

		derived.put("key1", "value1");
		derived.put("key2", "value2");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(DerivedMap.class)).hasSize(2);
		assertThat(testGenerator.renderTest(DerivedMap.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		DerivedMap<String, String> derived = new DerivedMap<>();

		derived.put("key1", "value1");
		derived.put("key2", "value2");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(DerivedMap.class).getTestCode())
			.containsPattern(""
				+ "Matcher<\\?> after = new GenericMatcher\\(\\) \\{\\s*"
				+ "Object after = null;\\s*"
				+ "Matcher<\\?> before = recursive\\(Object.class\\);\\s*"
				+ "int hash = \\d+;\\s*"
				+ "Object key = \"key2\";\\s*"
				+ "Object next = null;\\s*"
				+ "Object value = \"value2\";\\s*"
				+ "\\}\\.matching\\(clazz\\(\"java.util.LinkedHashMap\\$Entry\"\\), Object.class\\);"
				)
			.doesNotContainPattern("Object key = \"key2\";\\s*Object key = \"key2\";");
	}

}