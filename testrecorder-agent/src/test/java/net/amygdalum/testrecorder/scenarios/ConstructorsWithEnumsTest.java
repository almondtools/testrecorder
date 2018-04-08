package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithEnums.ChainedEnum;
import net.amygdalum.testrecorder.scenarios.ConstructorsWithEnums.InnerEnum;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.ConstructorsWithEnums" })
public class ConstructorsWithEnumsTest {

	@Test
	public void testCompilable() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums("FIRST"));

		assertThat(string).isEqualTo("FIRST:FIRST");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class)).satisfies(testsRun());
	}

	@Test
	public void testEnumCompilable() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums(InnerEnum.FIRST));

		assertThat(string).isEqualTo("FIRST:null");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class)).satisfies(testsRun());
	}

	@Test
	public void testChainedEnumCompilable() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums(ChainedEnum.FIRST));

		assertThat(string).isEqualTo("FIRST:FIRST");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(ConstructorsWithEnums.class)).satisfies(testsRun());
	}

	@Test
	public void testCode() throws Exception {
		String string = ConstructorsWithEnums.toString(new ConstructorsWithEnums(ChainedEnum.SECOND));

		assertThat(string).isEqualTo("SECOND:SECOND");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(ConstructorsWithEnums.class))
			.hasSize(1)
			.first().satisfies(test -> assertThat(test)
				.containsWildcardPattern("new ConstructorsWithEnums(ChainedEnum.SECOND)")
				.containsWildcardPattern("SECOND:SECOND")
				.containsWildcardPattern("sameInstance(ChainedEnum.SECOND)")
				.containsWildcardPattern("sameInstance(InnerEnum.SECOND)"));
	}

}