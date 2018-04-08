package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.GenericFields" })
public class GenericFieldsTest {

	@Test
	public void testCompilableNonNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(new HashSet<>());

		assertThat(bean.hashCode()).isEqualTo(0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericFields.class)).satisfies(testsRun());
	}

	@Test
	public void testCompilableNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(null);

		assertThat(bean.hashCode()).isEqualTo(1);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericFields.class)).satisfies(testsRun());
	}

	@Test
	public void testCodeNonNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(new HashSet<>());

		assertThat(bean.hashCode()).isEqualTo(0);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericFields.class))
			.hasSize(1)
			.first().satisfies(test -> assertThat(test)
				.containsWildcardPattern("genericFields?.setSet(set?)")
				.contains("equalTo(0)")
				.contains("empty()"));
	}

	@Test
	public void testCodeNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(null);

		assertThat(bean.hashCode()).isEqualTo(1);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericFields.class))
			.hasSize(1)
			.first().satisfies(test -> assertThat(test)
				.contains("equalTo(1)")
				.contains("set = null"));
	}
}