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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.AlmostBean" })
public class AlmostBeanTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		AlmostBean bean = new AlmostBean();
		bean.setSTRING("str");

		assertThat(bean.hashCode()).isEqualTo(3);
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(AlmostBean.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		AlmostBean bean = new AlmostBean();
		bean.setSTRING("str");

		assertThat(bean.hashCode()).isEqualTo(3);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(AlmostBean.class)).hasSize(1);
		assertThat(testGenerator.testsFor(AlmostBean.class))
			.anySatisfy(test -> {
				System.out.println(test);
				assertThat(test)
					.contains("new AlmostBean()")
					.containsWildcardPattern("almostBean?.set")
					.contains("equalTo(3)");
			});
	}
}