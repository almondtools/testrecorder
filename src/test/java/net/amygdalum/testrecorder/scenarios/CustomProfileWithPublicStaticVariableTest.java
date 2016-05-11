package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes={
	"net.amygdalum.testrecorder.scenarios.OtherProfile",
	"net.amygdalum.testrecorder.scenarios.CustomProfileWithPublicStaticVariable"
	})
public class CustomProfileWithPublicStaticVariableTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}
	
	@Test
	public void testCompilable() throws Exception {
		CustomProfileWithPublicStaticVariable bean = new CustomProfileWithPublicStaticVariable();
		
		assertThat(bean.inc(), equalTo(1));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CustomProfileWithPublicStaticVariable.class), compiles(CustomProfileWithPublicStaticVariable.class));
		assertThat(testGenerator.renderTest(CustomProfileWithPublicStaticVariable.class), testsRun(CustomProfileWithPublicStaticVariable.class));
	}

}
