package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRuns;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.ConfigRegistry;
import net.amygdalum.testrecorder.DefaultConfig;
import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={"net.amygdalum.testrecorder.scenarios.CustomProfileWithPublicStaticVariable"})
public class CustomProfileWithPublicStaticVariableTest {

	@Before
	public void before() throws Exception {
		((TestGenerator) ConfigRegistry.loadConfig(DefaultConfig.class).getSnapshotConsumer()).clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		CustomProfileWithPublicStaticVariable bean = new CustomProfileWithPublicStaticVariable();
		
		assertThat(bean.inc(), equalTo(1));

		TestGenerator testGenerator = TestGenerator.fromRecorded(bean);
		assertThat(testGenerator.renderTest(CustomProfileWithPublicStaticVariable.class), compiles());
		assertThat(testGenerator.renderTest(CustomProfileWithPublicStaticVariable.class), testsRuns());
	}

}
