package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.iterables.IterableConditions.containingExactly;
import static net.amygdalum.extensions.assertj.strings.StringConditions.containing;
import static net.amygdalum.extensions.assertj.strings.StringConditions.containingWildcardPattern;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.condition.AllOf.allOf;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.AmbiguousConstructorBean" })
public class AmbiguousConstructorBeanTest {

	@Test
	public void testCompilable() throws Exception {
		AmbiguousConstructorBean bean = new AmbiguousConstructorBean(22, 0, new AmbiguousConstructorBean(2, 4, null));

		assertThat(bean.hashCode()).isEqualTo(217);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(AmbiguousConstructorBean.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		AmbiguousConstructorBean bean = new AmbiguousConstructorBean(22, 0, new AmbiguousConstructorBean(2, 4, null));

		assertThat(bean.hashCode()).isEqualTo(217);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(AmbiguousConstructorBean.class))
		.hasSize(2)
		.is(containingExactly(
			allOf(
				containingWildcardPattern("new AmbiguousConstructorBean(2, 4, null)"),
				containing("equalTo(15)")),
			allOf(
				containingWildcardPattern("new AmbiguousConstructorBean(22, 0, ambiguousConstructorBean?)"),
				containing("equalTo(217)"))));
	}

}