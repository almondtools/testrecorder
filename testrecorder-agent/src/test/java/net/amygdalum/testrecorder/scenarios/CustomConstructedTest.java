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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.CustomConstructed" })
public class CustomConstructedTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		CustomConstructed bean = new CustomConstructed();
		bean.string("str");

		assertThat(bean.hashCode()).isEqualTo(3);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CustomConstructed.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		CustomConstructed bean = new CustomConstructed();
		bean.string("str");

		assertThat(bean.hashCode()).isEqualTo(3);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CustomConstructed.class)).hasSize(1);
		assertThat(testGenerator.testsFor(CustomConstructed.class))
			.anySatisfy(test -> {
				assertThat(test)
					.contains("new CustomConstructed()")
					.containsWildcardPattern("customConstructed?.string")
					.contains("equalTo(3)");
			});
	}

	@Test
	public void testCodeGeneric() throws Exception {
		CustomConstructed bean = new CustomConstructed();
		bean.other("str");

		assertThat(bean.hashCode()).isEqualTo(3);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(CustomConstructed.class)).hasSize(1);
		assertThat(testGenerator.testsFor(CustomConstructed.class))
			.anySatisfy(test -> {
				assertThat(test)
					.doesNotContain("new CustomConstructed()")
					.containsWildcardPattern("new GenericObject() {*other = \"str\";*}")
					.contains("equalTo(3)");
			});
	}
}