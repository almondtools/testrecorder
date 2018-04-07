package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.testrecorder.test.TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = { "net.amygdalum.testrecorder.scenarios.Arguments" })
public class ArgumentsTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		Arguments args = new Arguments();
		String result = ""
			+ args.primitive(1)
			+ args.towordprimitive(2l)
			+ args.object("3")
			+ args.towordprimitiveAndObject(4d, "5")
			+ args.mixed("6", 7l, 8, 9d);

		assertThat(result).isEqualTo("1234.056789.0");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Arguments.class)).hasSize(5);
		assertThat(testGenerator.renderTest(Arguments.class)).satisfies(testsRun());
	}

}