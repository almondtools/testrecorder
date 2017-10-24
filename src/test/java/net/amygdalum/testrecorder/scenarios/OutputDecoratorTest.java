package net.amygdalum.testrecorder.scenarios;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.conmatch.strings.WildcardStringMatcher;

import net.amygdalum.testrecorder.runtime.OutputDecorator;
import net.amygdalum.testrecorder.runtime.Throwables;

public class OutputDecoratorTest {

	@Test
	public void testMissingOutputRecording() throws Exception {
		Outputs outputs = new Outputs();
		Outputs outputsDecorated = new OutputDecorator<>(outputs)
			.expect("print", "net.amygdalum.testrecorder.scenarios.Outputs.recorded", new Class[] { String.class }, null, equalTo("Hello "))
			.expect("print", "net.amygdalum.testrecorder.scenarios.Outputs.recorded", new Class[] { String.class }, null, equalTo("World"))
			.end();

		Throwable exception = Throwables.capture(() -> outputsDecorated.notrecorded());

		assertThat(exception.getMessage(), WildcardStringMatcher.containsPattern("requested input from caller <*net.amygdalum.testrecorder.scenarios.Outputs.notrecorded*> was not recorded. Expected caller <net.amygdalum.testrecorder.scenarios.Outputs.recorded>. Ensure that all call sites are recorded"));
	}

	@Test
	public void testUnexpectedOutput() throws Exception {
		Outputs outputs = new Outputs();
		Outputs outputsDecorated = new OutputDecorator<>(outputs)
			.expect("print", "net.amygdalum.testrecorder.scenarios.Outputs.recorded", new Class[] { String.class }, null, equalTo("Hello "))
			.expect("print", "net.amygdalum.testrecorder.scenarios.Outputs.recorded", new Class[] { String.class }, null, equalTo("Welt"))
			.end();

		Throwable exception = Throwables.capture(() -> outputsDecorated.recorded());

		assertThat(exception.getMessage(), equalTo("expected output:\nprint(\"Welt\")\nbut found:\nprint(\"World\")"));
	}

	@Test
	public void testMissingOutput() throws Exception {
		Outputs outputs = new Outputs();
		Outputs outputsDecorated = new OutputDecorator<>(outputs)
			.expect("print", "net.amygdalum.testrecorder.scenarios.Outputs.recorded", new Class[] { String.class }, null, equalTo("Hello "))
			.expect("print", "net.amygdalum.testrecorder.scenarios.Outputs.recorded", new Class[] { String.class }, null, equalTo("World"))
			.expect("print", "net.amygdalum.testrecorder.scenarios.Outputs.recorded", new Class[] { String.class }, null, equalTo("!"))
			.end();

		assertThat(outputsDecorated, not(OutputDecorator.verifies()));
	}
}
