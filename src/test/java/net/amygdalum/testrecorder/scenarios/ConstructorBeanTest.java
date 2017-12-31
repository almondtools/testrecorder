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
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.ConstructorBean"})
public class ConstructorBeanTest {

	
	
	@Test
	public void testCompilable() throws Exception {
		ConstructorBean bean = new ConstructorBean(22, new ConstructorBean(0, null));
		
		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorBean.class), compiles(ConstructorBean.class));
		assertThat(testGenerator.renderTest(ConstructorBean.class), testsRun(ConstructorBean.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		ConstructorBean bean = new ConstructorBean(22, new ConstructorBean(0, null));
		
		assertThat(bean.hashCode()).isEqualTo(191);

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ConstructorBean.class), hasSize(2));
		assertThat(testGenerator.testsFor(ConstructorBean.class), containsInAnyOrder(
			allOf(containsPattern("new ConstructorBean(0, null)"), containsString("equalTo(13)")), 
			allOf(containsPattern("new ConstructorBean(22, constructorBean?)"), containsString("equalTo(191)"))));
	}

}