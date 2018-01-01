package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ClassicBean" })
public class ClassicBeanTest {

	@Test
	public void testCompilable() throws Exception {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO(new ClassicBean());

		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ClassicBean.class), compiles(ClassicBean.class));
		assertThat(testGenerator.renderTest(ClassicBean.class), testsRun(ClassicBean.class));
	}

	@Test
	public void testCode() throws Exception {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO(new ClassicBean());

		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ClassicBean.class)).hasSize(2);
		assertThat(testGenerator.testsFor(ClassicBean.class))
			.anySatisfy(test -> {
				assertThat(test)
					.contains("new ClassicBean()")
					.doesNotContainWildcardPattern("classicBean?.set")
					.contains("equalTo(13)");
			})
			.anySatisfy(test -> {
				assertThat(test)
					.containsWildcardPattern("classicBean?.setI(22)")
					.containsWildcardPattern("classicBean?.setO(classicBean?)")
					.contains("equalTo(191)");
			});
	}
}