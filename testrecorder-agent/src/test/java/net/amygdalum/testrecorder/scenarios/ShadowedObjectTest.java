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
@Instrumented(classes = {
	"net.amygdalum.testrecorder.scenarios.ShadowedObject",
	"net.amygdalum.testrecorder.scenarios.ShadowingObject",
	"net.amygdalum.testrecorder.scenarios.Other$ShadowingObject"
})
public class ShadowedObjectTest {

	@Test
	public void testCompilable() throws Exception {
		ShadowingObject object = new ShadowingObject("field");

		assertThat(object.toString()).isEqualTo("field > 42");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ShadowingObject.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		ShadowingObject object = new ShadowingObject("field");

		assertThat(object.toString()).isEqualTo("field > 42");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ShadowingObject.class))
			.hasSize(1)
			.first().satisfies(test -> assertThat(test)
				.containsWildcardPattern(""
					+ "new GenericObject() {*"
					+ "ShadowedObject$field*42*"
					+ "ShadowingObject$field*\"field\"*"
					+ "}"));
	}

	@Test
	public void testCodeDoubleHidden() throws Exception {
		Other.ShadowingObject object = new Other.ShadowingObject(42);

		assertThat(object.toString()).isEqualTo("42 > field > 42");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Other.class)).hasSize(2);
		assertThat(testGenerator.testsFor(Other.class))
			.anySatisfy(test -> assertThat(test)
				.containsWildcardPattern(""
					+ "new GenericObject() {*"
					+ "int net$amygdalum$testrecorder$scenarios$Other$ShadowingObject$field = 42;*"
					+ "int net$amygdalum$testrecorder$scenarios$ShadowedObject$field = 42;*"
					+ "String net$amygdalum$testrecorder$scenarios$ShadowingObject$field = \"field\";*"
					+ "}"));
	}

}
