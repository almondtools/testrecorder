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
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.HiddenInnerClass" })
public class HiddenInnerClassTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");

		assertThat(object.toString(), equalTo("hidden name"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		System.out.println(testGenerator.testsFor(HiddenInnerClass.class));
		assertThat(testGenerator.renderTest(HiddenInnerClass.class), compiles(HiddenInnerClass.class));
		assertThat(testGenerator.renderTest(HiddenInnerClass.class), testsRun(HiddenInnerClass.class));
	}

	@Test
	public void testCode() throws Exception {
		HiddenInnerClass object = new HiddenInnerClass("hidden name");

		assertThat(object.toString(), equalTo("hidden name"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(HiddenInnerClass.class), hasSize(1));
		assertThat(testGenerator.testsFor(HiddenInnerClass.class), contains(allOf(
			containsPattern("Wrapped hidden? = new GenericObject() {*String name = \"hidden name\";*}.as(clazz(\"net.amygdalum.testrecorder.scenarios.HiddenInnerClass$Hidden\"));"),
			containsPattern("HiddenInnerClass hiddenInnerClass? = new GenericObject() {*Wrapped o = hidden2;*}.as(HiddenInnerClass.class)"),
			containsPattern("new GenericMatcher() {*"
				+ "Matcher<?> o = new GenericMatcher() {*"
				+ "String name = \"hidden name\";*"
				+ "}.matching(clazz(\"net.amygdalum.testrecorder.scenarios.HiddenInnerClass$Hidden\"));*"
				+ "}"
				+ ".matching(HiddenInnerClass.class));"))));
	}
}