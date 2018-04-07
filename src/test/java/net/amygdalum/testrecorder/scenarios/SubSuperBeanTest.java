package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.iterables.IterableConditions.containingExactly;
import static net.amygdalum.extensions.assertj.strings.StringConditions.containing;
import static net.amygdalum.extensions.assertj.strings.StringConditions.containingWildcardPattern;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.not;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

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
		assertThat(testGenerator.testsFor(SubBean.class)).is(containingExactly(
			allOf(
				containing("new SubBean()"),
				not(containingWildcardPattern("subBean?.set")),
				containing("equalTo(13)")),
			allOf(
				containingWildcardPattern("subBean?.setI(22)"),
				containingWildcardPattern("subBean?.setO(subBean?)"),
				containing("equalTo(191)"))));
	}
}