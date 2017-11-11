package net.amygdalum.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.almondtools.conmatch.exceptions.ExceptionMatcher;

import mockit.Invocation;
import mockit.Mock;
import net.amygdalum.testrecorder.runtime.FakeCalls;
import net.amygdalum.testrecorder.runtime.FakeClass;
import net.amygdalum.testrecorder.runtime.FakeIn;
import net.amygdalum.testrecorder.runtime.FakeOut;
import net.amygdalum.testrecorder.runtime.Throwables;

public class FakeClassTest {

	@Test
	public void testInputs() throws Exception {
		Inputs inputs = new Inputs();
		FakeClass<Inputs> faked = new FakeClass<Inputs>() {

			FakeCalls<String> read = new FakeIn<String>(inputs, "read", new Class[0])
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", "Hello")
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", " ")
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", "World");

			@Mock
			public String read(Invocation invocation) {
				return read.next(invocation);
			}
		};

		String result = inputs.recorded();

		assertThat(result, equalTo("Hello World"));
		faked.verify();
	}

	@Test
	public void testInputsWithSideEffects() throws Exception {
		Inputs inputs = new Inputs();
		FakeClass<Inputs> faked = new FakeClass<Inputs>() {

			FakeCalls<Void> read = new FakeIn<Void>(inputs, "read", new Class[] {char[].class})
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.sideEffectsRecorded", null, "Hello World".toCharArray());

			@Mock
			public void read(Invocation invocation, char[] cs) {
				read.next(invocation);
			}
		};

		String result = inputs.sideEffectsRecorded();

		assertThat(result, equalTo("Hello World"));
		faked.verify();
	}

	@Test
	public void testInputsWithSideObjectEffects() throws Exception {
		Inputs inputs = new Inputs();
		ArrayList<String> list = new ArrayList<>();
		list.add("Hello");
		list.add("World");
		FakeClass<Inputs> faked = new FakeClass<Inputs>() {
			
			FakeCalls<Void> read = new FakeIn<Void>(inputs, "read", new Class[] {List.class})
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.objectSideEffectsRecorded", null, list);
			
			@Mock
			public void read(Invocation invocation, List<String> s) {
				read.next(invocation);
			}
		};
		
		String result = inputs.objectSideEffectsRecorded();
		
		assertThat(result, equalTo("[Hello, World]"));
		faked.verify();
	}
	
	@Test
	public void testMissingInputRecording() throws Exception {
		Inputs inputs = new Inputs();
		FakeClass<Inputs> faked = new FakeClass<Inputs>() {

			FakeCalls<String> read = new FakeIn<String>(inputs, "read", new Class[0])
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", "Hello")
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", "")
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", "World");

			@Mock
			public String read(Invocation invocation) {
				return read.next(invocation);
			}
		};

		Throwable exception = Throwables.capture(() -> inputs.notrecorded());

		assertThat(exception.getMessage(), containsPattern("missing input for:\n*called from*\n\nIf the input was recorded ensure that all call sites are recorded"));
		assertThat(Throwables.capture(faked::verify), ExceptionMatcher.matchesException(AssertionError.class)
			.withMessage(containsPattern("expected but not found"
				+ "*"
				+ "read()"
				+ "*"
				+ "read()"
				+ "*"
				+ "read()")));
	}
	
	@Test
	public void testMissingInput() throws Exception {
		Inputs inputs = new Inputs();
		FakeClass<Inputs> faked = new FakeClass<Inputs>() {

			FakeCalls<String> read = new FakeIn<String>(inputs, "read", new Class[0])
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", "Hello")
				.provide("net.amygdalum.testrecorder.scenarios.Inputs.recorded", "");

			@Mock
			public String read(Invocation invocation) {
				return read.next(invocation);
			}
		};

		Throwable exception = Throwables.capture(() -> inputs.recorded());

		assertThat(exception.getMessage(), containsPattern("missing input for:\n*called from*\n\nIf the input was recorded ensure that all call sites are recorded"));
		faked.verify();
	}

	@Test
	public void testStandardLibInput() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		FakeClass<System> faked = new FakeClass<System>() {

			FakeCalls<Long> read = new FakeIn<Long>(System.class, "currentTimeMillis", new Class[0])
				.provide("net.amygdalum.testrecorder.scenarios.StandardLibInputOutput.getTimestamp", 42l);

			@Mock
			public long currentTimeMillis(Invocation invocation) {
				return read.next(invocation);
			}
		};

		long result = io.getTimestamp();

		assertThat(result, equalTo(42l));
		
		faked.verify();
	}

	@Test
	public void testOutputs() throws Exception {
		Outputs outputs = new Outputs();
		FakeClass<Outputs> faked = new FakeClass<Outputs>() {

			FakeCalls<Void> print = new FakeOut<Void>(outputs, "print", new Class[] { String.class })
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("Hello "))
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("World"));

			@Mock
			public void print(Invocation invocation, String s) {
				print.next(invocation, s);
			}
		};

		outputs.recorded();

		faked.verify();
	}

	@Test
	public void testMissingOutputRecording() throws Exception {
		Outputs outputs = new Outputs();
		FakeClass<Outputs> faked = new FakeClass<Outputs>() {

			FakeCalls<Void> print = new FakeOut<Void>(outputs, "print", new Class[] { String.class })
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("Hello "))
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("World"));

			@Mock
			public void print(Invocation invocation, String s) {
				print.next(invocation, s);
			}
		};

		Throwable exception = Throwables.capture(() -> outputs.notrecorded());

		assertThat(exception.getMessage(), containsPattern("missing input for:\n*called from*\n\nIf the input was recorded ensure that all call sites are recorded"));
		assertThat(Throwables.capture(faked::verify), ExceptionMatcher.matchesException(AssertionError.class)
			.withMessage(containsPattern("expected but not found"
				+ "*"
				+ "print(\"Hello \")"
				+ "*"
				+ "print(\"World\")")));
	}

	@Test
	public void testUnexpectedOutput() throws Exception {
		Outputs outputs = new Outputs();
		FakeClass<Outputs> faked = new FakeClass<Outputs>() {

			FakeCalls<Void> print = new FakeOut<Void>(outputs, "print", new Class[] { String.class })
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("Hello "))
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("Welt"));

			@Mock
			public void print(Invocation invocation, String s) {
				print.next(invocation, s);
			}
		};

		Throwable exception = Throwables.capture(() -> outputs.recorded());

		assertThat(exception.getMessage(), equalTo("expected output:\nprint(\"Welt\")\nbut found:\nprint(\"World\")"));
		assertThat(Throwables.capture(faked::verify), ExceptionMatcher.matchesException(AssertionError.class)
			.withMessage(containsPattern("expected but not found"
				+ "*"
				+ "print(\"Welt\")")));
	}
	
	@Test
	public void testMissingOutput() throws Exception {
		Outputs outputs = new Outputs();
		FakeClass<Outputs> faked = new FakeClass<Outputs>() {

			FakeCalls<Void> print = new FakeOut<Void>(outputs, "print", new Class[] { String.class })
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("Hello "))
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("World"))
				.provide("net.amygdalum.testrecorder.scenarios.Outputs.recorded", null, equalTo("!"));

			@Mock
			public void print(Invocation invocation, String s) {
				print.next(invocation, s);
			}
		};

		assertThat(Throwables.capture(faked::verify), ExceptionMatcher.matchesException(AssertionError.class)
			.withMessage(containsPattern("expected but not found"
				+ "*"
				+ "print(\"Hello \")"
				+ "*"
				+ "print(\"World\")"
				+ "*"
				+ "print(\"!\")")));
	}

	@Test
	public void testStandardLibOutput() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		FakeClass<ByteArrayOutputStream> faked = new FakeClass<ByteArrayOutputStream>() {

			FakeCalls<Void> write = new FakeOut<Void>(ByteArrayOutputStream.class, "write", new Class[] {byte[].class})
				.provide("net.amygdalum.testrecorder.scenarios.StandardLibInputOutput.store", null, "My Output".getBytes());

			@Mock
			public void write(Invocation invocation, byte[] value) {
				write.next(invocation);
			}
		};

		io.store("My Output");

		faked.verify();
	}
	
}
