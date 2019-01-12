package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.test.JUnit4TestsRun.testsRun;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.integration.Instrumented;
import net.amygdalum.testrecorder.integration.TestRecorderAgentExtension;
import net.amygdalum.testrecorder.util.testobjects.Container;

@ExtendWith(TestRecorderAgentExtension.class)
@Instrumented(classes = {"net.amygdalum.testrecorder.scenarios.Arguments"})
public class ArgumentsTest {

	@Test
	public void testCompilesAndRuns() throws Exception {
		Arguments args = new Arguments();
		String result = ""
			+ args.primitive(1)
			+ args.towordprimitive(2l)
			+ args.string("3")
			+ args.towordprimitiveAndString(4d, "5")
			+ args.mixed("6", 7l, 8, 9d);

		assertThat(result).isEqualTo("1234.056789.0");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.testsFor(Arguments.class)).hasSize(5);
		assertThat(testGenerator.renderTest(Arguments.class)).satisfies(testsRun());
	}

	@Test
	public void testReportingOfNoChanges() throws Exception {
		Arguments args = new Arguments();

		Container<String> object = new Container<>("initial");

		String result = args.argumentNoModification(object);

		assertThat(result).isEqualTo("initial");
		assertThat(object.getContent()).isEqualTo("initial");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Arguments.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(Arguments.class).getTestCode())
			.containsWildcardPattern("assertThat(\"expected no change, but was:\", container?, new GenericMatcher() {*Object content = \"initial\";*}.matching(Container.class));");
	}

	@Test
	public void testReportingOfChanges() throws Exception {
		Arguments args = new Arguments();

		Container<String> object = new Container<>("initial");

		String result = args.argumentModification(object, "new");

		assertThat(result).isEqualTo("new");
		assertThat(object.getContent()).isEqualTo("new");

		TestGenerator testGenerator = TestGenerator.fromRecorded();
		assertThat(testGenerator.renderTest(Arguments.class)).satisfies(testsRun());
		assertThat(testGenerator.renderTest(Arguments.class).getTestCode())
			.containsWildcardPattern("assertThat(\"expected change:\", container?, new GenericMatcher() {*Object content = \"new\";*}.matching(Container.class));");
	}

}