package com.almondtools.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
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

import com.almondtools.testrecorder.ConfigRegistry;
import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.TestGenerator;
import com.almondtools.testrecorder.util.Instrumented;
import com.almondtools.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"com.almondtools.testrecorder.scenarios.SuperBean", "com.almondtools.testrecorder.scenarios.SubBean"})
public class SubSuperBeanTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO(new SubBean());

		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded(bean);
		assertThat(testGenerator.renderTest(SubBean.class), compiles());
		assertThat(testGenerator.renderTest(SubBean.class), testsRuns());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO(new SubBean());

		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded(bean);
		assertThat(testGenerator.testsFor(SubBean.class), hasSize(2));
		assertThat(testGenerator.testsFor(SubBean.class), containsInAnyOrder(
			allOf(containsString("new SubBean()"), not(containsPattern("subBean?.set")), containsString("equalTo(13)")),
			allOf(containsPattern("subBean?.setI(22)"), containsPattern("subBean?.setO(subBean?)"), containsString("equalTo(191)"))));
	}
}