package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.allOf;
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
	    CustomProfileWithPublicStaticVariable.istr = "0";
	    CustomProfileWithPublicStaticVariable.iAnnotated = 0;
	}
	
	@Test
	public void testCompilable() throws Exception {
		CustomProfileWithPublicStaticVariable bean = new CustomProfileWithPublicStaticVariable();
		
		assertThat(bean.inc(), equalTo(1));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(CustomProfileWithPublicStaticVariable.class), compiles(CustomProfileWithPublicStaticVariable.class));
    }
    
    @Test
    public void testRunnable() throws Exception {
        CustomProfileWithPublicStaticVariable bean = new CustomProfileWithPublicStaticVariable();
        
        assertThat(bean.inc(), equalTo(1));

        TestGenerator testGenerator = TestGenerator.fromRecorded();
        assertThat(testGenerator.renderTest(CustomProfileWithPublicStaticVariable.class), allOf(
            containsPattern("CustomProfileWithPublicStaticVariable.istr = \"0\";"),
            containsPattern("CustomProfileWithPublicStaticVariable.iAnnotated = 0;"),
            containsPattern("assertThat(CustomProfileWithPublicStaticVariable.istr, equalTo(\"1\"));"),
            containsPattern("assertThat(CustomProfileWithPublicStaticVariable.iAnnotated, equalTo(1));")));
		assertThat(testGenerator.renderTest(CustomProfileWithPublicStaticVariable.class), testsRun(CustomProfileWithPublicStaticVariable.class));
	}

}
