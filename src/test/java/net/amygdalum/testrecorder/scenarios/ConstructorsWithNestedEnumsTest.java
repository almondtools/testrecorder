package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.dynamiccompile.CompilableMatcher.compiles;
import static net.amygdalum.testrecorder.dynamiccompile.TestsRunnableMatcher.testsRun;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums.ChainedEnum;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums.InnerEnum;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums.RecursiveEnum;
import net.amygdalum.testrecorder.util.Instrumented;
import net.amygdalum.testrecorder.util.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ConstructorsWithNestedEnums" })
public class ConstructorsWithNestedEnumsTest {

	

	@Test
	public void testCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of("FIRST"));

		assertThat(string).isEqualTo("FIRST:FIRST");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

	@Test
	public void testEnumCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of(InnerEnum.FIRST));

		assertThat(string).isEqualTo("FIRST:null");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

	@Test
	public void testChainedEnumCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of(ChainedEnum.FIRST));
		
		assertThat(string).isEqualTo("FIRST:FIRST");
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

	@Test
	public void testRecursiveEnumCompilable() throws Exception {
		String string = ConstructorsWithNestedEnums.toString(ConstructorsWithNestedEnums.of(RecursiveEnum.THIRD));
		
		assertThat(string).isEqualTo("null:null:THIRD");
		
		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), compiles(ConstructorsWithNestedEnums.class));
		assertThat(testGenerator.renderTest(ConstructorsWithNestedEnums.class), testsRun(ConstructorsWithNestedEnums.class));
	}

}