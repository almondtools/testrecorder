package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.ClassicBean"})
public class ClassicBeanTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO(new ClassicBean());
		
		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ClassicBean.class), compiles(ClassicBean.class));
		assertThat(testGenerator.renderTest(ClassicBean.class), testsRun(ClassicBean.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO(new ClassicBean());
		
		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ClassicBean.class), hasSize(2));
		assertThat(testGenerator.testsFor(ClassicBean.class), containsInAnyOrder(
			allOf(containsString("new ClassicBean()"), not(containsPattern("classicBean?.set")), containsString("equalTo(13)")), 
			allOf(containsPattern("classicBean?.setI(22)"), containsPattern("classicBean?.setO(classicBean?)"), containsString("equalTo(191)"))));
	}
}