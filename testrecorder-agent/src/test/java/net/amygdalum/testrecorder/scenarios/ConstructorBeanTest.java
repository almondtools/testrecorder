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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ConstructorBean" })
public class ConstructorBeanTest {

	@Test
	public void testCompilable() throws Exception {
		ConstructorBean bean = new ConstructorBean(22, new ConstructorBean(0, null));

		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorBean.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		ConstructorBean bean = new ConstructorBean(22, new ConstructorBean(0, null));

		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ConstructorBean.class)).hasSize(2);
		assertThat(testGenerator.testsFor(ConstructorBean.class))
			.anySatisfy(test -> assertThat(test)
				.containsWildcardPattern("new ConstructorBean(0, null)")
				.contains("equalTo(13)"))
			.anySatisfy(test -> assertThat(test)
				.containsWildcardPattern("new ConstructorBean(22, constructorBean?)")
				.contains("equalTo(191)"));
	}

}