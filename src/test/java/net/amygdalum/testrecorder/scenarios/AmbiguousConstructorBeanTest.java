package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

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
		assertThat(testGenerator.renderTest(AmbiguousConstructorBean.class), compiles(AmbiguousConstructorBean.class));
		assertThat(testGenerator.renderTest(AmbiguousConstructorBean.class), testsRun(AmbiguousConstructorBean.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		AmbiguousConstructorBean bean = new AmbiguousConstructorBean(22, 0, new AmbiguousConstructorBean(2, 4, null));

		assertThat(bean.hashCode()).isEqualTo(217);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(AmbiguousConstructorBean.class), hasSize(2));
		assertThat(testGenerator.testsFor(AmbiguousConstructorBean.class), containsInAnyOrder(
			allOf(containsPattern("new AmbiguousConstructorBean(2, 4, null)"), containsString("equalTo(15)")),
			allOf(containsPattern("new AmbiguousConstructorBean(22, 0, ambiguousConstructorBean?)"), containsString("equalTo(217)"))));
	}

}