package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.InstrumentedClassLoaderRunner;

@RunWith(InstrumentedClassLoaderRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.GenericFields" })
public class GenericFieldsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilableNonNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(new HashSet<>());

		assertThat(bean.hashCode(), equalTo(0));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericFields.class), compiles(GenericFields.class));
		assertThat(testGenerator.renderTest(GenericFields.class), testsRun(GenericFields.class));
	}

	@Test
	public void testCompilableNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(null);

		assertThat(bean.hashCode(), equalTo(1));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(GenericFields.class), compiles(GenericFields.class));
		assertThat(testGenerator.renderTest(GenericFields.class), testsRun(GenericFields.class));
	}

	@Test
	public void testCodeNonNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(new HashSet<>());

		assertThat(bean.hashCode(), equalTo(0));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericFields.class), hasSize(1));
		assertThat(testGenerator.testsFor(GenericFields.class), contains(allOf(
			containsPattern("genericFields?.setSet(set?)"),
			containsString("equalTo(0)"),
			containsString("empty()"))));
	}

	@Test
	public void testCodeNull() throws Exception {
		GenericFields bean = new GenericFields();
		bean.setSet(null);

		assertThat(bean.hashCode(), equalTo(1));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(GenericFields.class), hasSize(1));
		assertThat(testGenerator.testsFor(GenericFields.class), contains(allOf(
			containsString("equalTo(1)"),
			containsString("set = null"))));
	}
}