package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.runtime.InputDecorator;
import net.amygdalum.testrecorder.runtime.Throwables;

public class InputDecoratorTest {

	@Test
	public void testMissingInputRecording() throws Exception {
		Inputs inputs = new Inputs();
		Inputs inputsDecorated = new InputDecorator<>(inputs)
			.provide("read", "net.amygdalum.testrecorder.scenarios.Inputs.recorded", new Class[0], "Hello", empty())
			.provide("read", "net.amygdalum.testrecorder.scenarios.Inputs.recorded", new Class[0], "", empty())
			.provide("read", "net.amygdalum.testrecorder.scenarios.Inputs.recorded", new Class[0], "World", empty())
			.setup();

		Throwable exception = Throwables.capture(() -> inputsDecorated.notrecorded());

		assertThat(exception.getMessage(), containsPattern("requested input from caller <*net.amygdalum.testrecorder.scenarios.Inputs.notrecorded*> was not recorded. Expected caller <net.amygdalum.testrecorder.scenarios.Inputs.recorded>. Ensure that all call sites are recorded"));
	}
	

	@Test
	public void testMissingInput() throws Exception {
		Inputs inputs = new Inputs();
		Inputs inputsDecorated = new InputDecorator<>(inputs)
			.provide("read", "net.amygdalum.testrecorder.scenarios.Inputs.recorded", new Class[0], "Hello", empty())
			.provide("read", "net.amygdalum.testrecorder.scenarios.Inputs.recorded", new Class[0], "", empty())
			.setup();

		Throwable exception = Throwables.capture(() -> inputsDecorated.recorded());

		assertThat(exception.getMessage(), equalTo("missing input for:\nread()\n\nIf the input was recorded ensure that all call sites are recorded"));
	}

}
