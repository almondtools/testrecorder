package net.amygdalum.testrecorder.scenarios;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.runtime.OutputDecorator;
import net.amygdalum.testrecorder.runtime.Throwables;

public class OutputDecoratorTest {

	@Test
	public void testUnexpectedOutput() throws Exception {
		Outputs outputs = new Outputs();
		Outputs outputsDecorated = new OutputDecorator<>(outputs)
			.expect("print", new Class[] { String.class }, null, equalTo("Hello "))
			.expect("print", new Class[] { String.class }, null, equalTo("Welt"))
			.end();

		Throwable exception = Throwables.capture(() -> outputsDecorated.recorded());

		assertThat(exception.getMessage(), equalTo("expected output:\nprint(\"Welt\")\nbut found:\nprint(\"World\")"));
	}

	@Test
	public void testMissingOutput() throws Exception {
		Outputs outputs = new Outputs();
		Outputs outputsDecorated = new OutputDecorator<>(outputs)
			.expect("print", new Class[] { String.class }, null, equalTo("Hello "))
			.expect("print", new Class[] { String.class }, null, equalTo("World"))
			.expect("print", new Class[] { String.class }, null, equalTo("!"))
			.end();

		assertThat(outputsDecorated, not(OutputDecorator.verifies()));
	}
}
