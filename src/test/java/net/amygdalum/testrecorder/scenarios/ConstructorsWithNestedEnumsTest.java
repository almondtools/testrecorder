package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums.ChainedEnum;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums.InnerEnum;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums.RecursiveEnum;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestrecorderAgentRunner;

@RunWith(TestrecorderAgentRunner.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums" })
public class ConstructorsWithNestedEnumsTest {

	@Before
	public void before() throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	@Test
	public void testCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of("FIRST"));

		assertThat(string, equalTo("FIRST:FIRST"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

	@Test
	public void testEnumCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of(InnerEnum.FIRST));

		assertThat(string, equalTo("FIRST:null"));

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

	@Test
	public void testChainedEnumCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of(ChainedEnum.FIRST));
		
		assertThat(string, equalTo("FIRST:FIRST"));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

	@Test
	public void testRecursiveEnumCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of(RecursiveEnum.THIRD));
		
		assertThat(string, equalTo("null:null:THIRD"));
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

}