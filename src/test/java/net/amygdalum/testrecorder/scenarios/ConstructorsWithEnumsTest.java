package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithEnums.ChainedEnum;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithEnums.InnerEnum;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ConstructorsWithEnums" })
public class ConstructorsWithEnumsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums("FIRST"));

		assertThat(string, equalTo("FIRST:FIRST"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class), compiles(ConstructorsWithEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class), testsRun(ConstructorsWithEnums.class));
	}

	@Test
	public void testEnumCompilable() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums(InnerEnum.FIRST));

		assertThat(string, equalTo("FIRST:null"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class), compiles(ConstructorsWithEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class), testsRun(ConstructorsWithEnums.class));
	}

	@Test
	public void testChainedEnumCompilable() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums(ChainedEnum.FIRST));

		assertThat(string, equalTo("FIRST:FIRST"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class), compiles(ConstructorsWithEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class), testsRun(ConstructorsWithEnums.class));
	}

	@Test
	public void testCode() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums(ChainedEnum.SECOND));

		assertThat(string, equalTo("SECOND:SECOND"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ConstructorsWithEnums.class), hasSize(1));
		assertThat(testGenerator.testsFor(ConstructorsWithEnums.class), contains(
			allOf(
				containsPattern("new ConstructorsWithEnums(ChainedEnum.SECOND)"),
				containsPattern("SECOND:SECOND"),
				containsPattern("sameInstance(ChainedEnum.SECOND)"),
				containsPattern("sameInstance(InnerEnum.SECOND)"))));
	}

}