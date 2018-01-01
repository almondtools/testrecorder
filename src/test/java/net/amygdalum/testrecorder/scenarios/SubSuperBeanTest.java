package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.testing.assertj.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.SuperBean", "net.amygdalum.testrecorder.scenarios.SubBean" })
public class SubSuperBeanTest {

	@Test
	public void testCompilable() throws Exception {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO(new SubBean());

		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(SubBean.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO(new SubBean());

		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(SubBean.class)).hasSize(2);
		assertThat(testGenerator.testsFor(SubBean.class)).iterate()
			.next().satisfies(test -> {
				assertThat(test).contains("new SubBean()");
				assertThat(test).doesNotContainWildcardPattern("subBean?.set");
				assertThat(test).contains("equalTo(13)");
			})
			.next().satisfies(test -> {
				assertThat(test).containsWildcardPattern("subBean?.setI(22)");
				assertThat(test).containsWildcardPattern("subBean?.setO(subBean?)");
				assertThat(test).contains("equalTo(191)");
			});

	}
}