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
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.PrivateInnerObject", "net.amygdalum.testrecorder.scenarios.PrivateInnerObject$InnerObject" })
public class PrivateInnerObjectTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		PrivateInnerObject object = new PrivateInnerObject();

		String value = object.method("str");
		assertThat(value, equalTo("str"));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		System.out.println(testGenerator.renderTest(PrivateInnerObject.class));
		assertThat(testGenerator.renderTest(PrivateInnerObject.class), compiles(PrivateInnerObject.class));
		assertThat(testGenerator.renderTest(PrivateInnerObject.class), testsRun(PrivateInnerObject.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCode() throws Exception {
		PrivateInnerObject object = new PrivateInnerObject();

		String value = object.method("str");
		assertThat(value, equalTo("str"));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(PrivateInnerObject.class), hasSize(1));
		assertThat(testGenerator.testsFor(PrivateInnerObject.class), contains(
			allOf(containsPattern("PrivateInnerObject.from(\"str2\")"))));
	}

}