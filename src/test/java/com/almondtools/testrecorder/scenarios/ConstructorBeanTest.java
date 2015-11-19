package com.almondtools.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.CoreMatchers.allOf;
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
@Instrumented(classes={"com.almondtools.testrecorder.scenarios.ConstructorBean"})
public class ConstructorBeanTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		ConstructorBean bean = new ConstructorBean(22, new ConstructorBean(0, null));
		
		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded(bean);
		assertThat(testGenerator.renderTest(ConstructorBean.class), compiles());
		assertThat(testGenerator.renderTest(ConstructorBean.class), testsRuns());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		ConstructorBean bean = new ConstructorBean(22, new ConstructorBean(0, null));
		
		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded(bean);
		assertThat(testGenerator.testsFor(ConstructorBean.class), hasSize(2));
		assertThat(testGenerator.testsFor(ConstructorBean.class), containsInAnyOrder(
			allOf(containsPattern("new ConstructorBean(0, null)"), containsString("equalTo(13)")), 
			allOf(containsPattern("new ConstructorBean(22, constructorBean?)"), containsString("equalTo(191)"))));
	}
}