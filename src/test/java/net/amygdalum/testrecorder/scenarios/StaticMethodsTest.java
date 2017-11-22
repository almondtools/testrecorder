package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.StaticMethods" })
public class StaticMethodsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		StaticMethods object = StaticMethods.from("str");

		assertThat(object.getValue(), equalTo("str"));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(StaticMethods.class), compiles(StaticMethods.class));
		assertThat(testGenerator.renderTest(StaticMethods.class), testsRun(StaticMethods.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		StaticMethods object = StaticMethods.from("str2");

		assertThat(object.getValue(), equalTo("str2"));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(StaticMethods.class), hasSize(1));
		assertThat(testGenerator.testsFor(StaticMethods.class), contains(
			allOf(containsPattern("StaticMethods.from(\"str2\")"))));
	}

}