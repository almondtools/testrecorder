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
@Instrumented(classes={"com.almondtools.testrecorder.scenarios.HiddenInnerClass", "com.almondtools.testrecorder.scenarios.HiddenInnerClass$Hidden"})
public class HiddenInnerClassTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");
		
		assertThat(object.toString(), equalTo("hidden name"));

		TestGenerator testGenerator = TestGenerator.fromRecorded(object);
		System.out.println(testGenerator.renderTest(HiddenInnerClass.class));
		assertThat(testGenerator.renderTest(HiddenInnerClass.class), compiles());
		assertThat(testGenerator.renderTest(HiddenInnerClass.class), testsRuns());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");
		
		assertThat(object.toString(), equalTo("hidden name"));

		TestGenerator testGenerator = TestGenerator.fromRecorded(object);
		assertThat(testGenerator.testsFor(HiddenInnerClass.class), hasSize(2));
		assertThat(testGenerator.testsFor(HiddenInnerClass.class), containsInAnyOrder(
			allOf(containsString("new HiddenInnerClass()"), not(containsPattern("HiddenInnerClass?.set")), containsString("equalTo(13)")), 
			allOf(containsPattern("HiddenInnerClass?.setI(22)"), containsPattern("HiddenInnerClass?.setO(HiddenInnerClass?)"), containsString("equalTo(191)"))));
	}
}