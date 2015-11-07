package com.almondtools.testrecorder.scenarios;

import static com.almondtools.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static com.almondtools.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.almondtools.testrecorder.ConfigRegistry;
import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;
import com.almondtools.testrecorder.TestGenerator;

public class BeanTest {

	private static SnapshotInstrumentor instrumentor;

	@BeforeClass
	public static void beforeClass() throws Exception {
		instrumentor = new SnapshotInstrumentor(new DefaultConfig());
		instrumentor.register("com.almondtools.testrecorder.scenarios.Bean");
	}
	
	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getMethodConsumer()).clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		Bean bean = new Bean();
		bean.setI(22);
		bean.setO(new Bean());
		
		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded(bean);
		assertThat(testGenerator.renderTest(Bean.class), compiles());
		assertThat(testGenerator.renderTest(Bean.class), testsRuns());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHashcode() throws Exception {
		Bean bean = new Bean();
		bean.setI(22);
		bean.setO(new Bean());
		
		assertThat(bean.hashCode(), equalTo(191));

		TestGenerator testGenerator = TestGenerator.fromRecorded(bean);
		assertThat(testGenerator.testsFor(Bean.class), hasSize(2));
		assertThat(testGenerator.testsFor(Bean.class), containsInAnyOrder(
			allOf(containsString("setI(0)"), containsString("setO(null)"), containsString("equalTo(13)")), 
			allOf(containsString("setI(22)"), containsString("setO(bean"), containsString("equalTo(191)"))));
	}
}