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
		assertThat(testGenerator.testsFor(AmbiguousConstructorBean.class)).hasSize(2);
		assertThat(testGenerator.testsFor(AmbiguousConstructorBean.class)).iterate()
			.next().satisfies(test -> assertThat(test)
				.containsWildcardPattern("new AmbiguousConstructorBean(2, 4, null)")
				.contains("equalTo(15)"))
			.next().satisfies(test -> assertThat(test)
				.containsWildcardPattern("new AmbiguousConstructorBean(22, 0, ambiguousConstructorBean?)")
				.contains("equalTo(217)"));
		assertThat(testGenerator.testsFor(AmbiguousConstructorBean.class)).iterate()
			.next().satisfies(s -> {
				assertThat(s).containsWildcardPattern("new AmbiguousConstructorBean(2, 4, null)");
				assertThat(s).contains("equalTo(15)");
			})
			.next().satisfies(s -> {
				assertThat(s).containsWildcardPattern("new AmbiguousConstructorBean(22, 0, ambiguousConstructorBean?)");
				assertThat(s).contains("equalTo(217)");
			});
	}

}