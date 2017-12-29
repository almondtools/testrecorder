package net.amygdalum.testrecorder.runtime;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.ioscenarios.Inputs;
import net.amygdalum.testrecorder.ioscenarios.Outputs;
import net.amygdalum.testrecorder.ioscenarios.StandardLibInputOutput;

@SuppressWarnings("unused")
public class FakeIOTest {

	@Test
	public void testInputs() throws Exception {
		Inputs inputs = new Inputs();
		FakeIO faked = FakeIO.fake(Inputs.class)
			.fakeInput(new Aspect() {
				public String read() {
					return null;
				}
			})
			.add(Inputs.class, "recorded", 24, "Hello")
			.add(Inputs.class, "recorded", 25, " ")
			.add(Inputs.class, "recorded", 26, "World")
			.setup();

		String result = inputs.recorded();

		assertThat(result, equalTo("Hello World"));
		faked.verify();
	}

	@Test
	public void testPrimitiveInputs() throws Exception {
		Inputs inputs = new Inputs();
		FakeIO faked = FakeIO.fake(Inputs.class)
			.fakeInput(new Aspect() {
				public boolean readBoolean() {
					return false;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 41, true)
			.fakeInput(new Aspect() {
				public byte readByte() {
					return 0;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 42, (byte) 2)
			.fakeInput(new Aspect() {
				public short readShort() {
					return 0;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 43, (short) 3)
			.fakeInput(new Aspect() {
				public int readInt() {
					return 0;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 44, 4)
			.fakeInput(new Aspect() {
				public long readLong() {
					return 0;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 45, 5l)
			.fakeInput(new Aspect() {
				public float readFloat() {
					return 0;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 46, 6f)
			.fakeInput(new Aspect() {
				public double readDouble() {
					return 0;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 47, 7d)
			.fakeInput(new Aspect() {
				public char readChar() {
					return 0;
				}
			})
			.add(Inputs.class, "primitivesRecorded", 48, 'x')
			.setup();

		String result = inputs.primitivesRecorded();

		assertThat(result, equalTo(""
			+ "boolean:true"
			+ "byte:2"
			+ "short:3"
			+ "int:4"
			+ "long:5"
			+ "float:6.0"
			+ "double:7.0"
			+ "char:x"));
		faked.verify();
	}

	@Test
	public void testInputsWithSideEffects() throws Exception {
		Inputs inputs = new Inputs();
		FakeIO faked = FakeIO.fake(Inputs.class)
			.fakeInput(new Aspect() {
				public void read(char[] cs) {
					return;
				}
			})
			.add(Inputs.class, "sideEffectsRecorded", 61, null, "Hello World".toCharArray())
			.setup();

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
		FakeIO faked = FakeIO.fake(Inputs.class)
			.fakeInput(new Aspect() {
				public void read(List<String> s) {
					return;
				}
			})
			.add(Inputs.class, "objectSideEffectsRecorded", 68, null, list)
			.setup();

		String result = inputs.objectSideEffectsRecorded();

		assertThat(result, equalTo("[Hello, World]"));
		faked.verify();
	}

	@Test
	public void testMissingInputRecording() throws Exception {
		Inputs inputs = new Inputs();
		FakeIO faked = FakeIO.fake(Inputs.class)
			.fakeInput(new Aspect() {
				public String read() {
					return null;
				}
			})
			.add(Inputs.class, "recorded", 24, "Hello")
			.add(Inputs.class, "recorded", 25, " ")
			.add(Inputs.class, "recorded", 26, "World")
			.setup();

		Throwable exception = Throwables.capture(() -> inputs.notrecorded());

		assertThat(exception.getMessage(), containsPattern("missing input for:"
			+ "\n*called from*"
			+ "\n"
			+ "\nIf the input was recorded ensure that all call sites are recorded"));
		assertThat(Throwables.capture(faked::verify), matchesException(AssertionError.class)
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
		FakeIO faked = FakeIO.fake(Inputs.class)
			.fakeInput(new Aspect() {
				public String read() {
					return null;
				}
			})
			.add(Inputs.class, "recorded", 24, "Hello")
			.add(Inputs.class, "recorded", 25, "")
			.setup();

		Throwable exception = Throwables.capture(() -> inputs.recorded());

		assertThat(exception.getMessage(), containsPattern("missing input for:"
			+ "\n*called from*"
			+ "\n"
			+ "\nIf the input was recorded ensure that all call sites are recorded"));
		faked.verify();
	}

	@Test
	public void testStandardLibInput() throws Exception {
		StandardLibInputOutput io = new StandardLibInputOutput();
		FakeIO faked = FakeIO.fake(System.class)
			.fakeInput(new Aspect() {
				public long currentTimeMillis() {
					return 0l;
				}
			})
			.add(StandardLibInputOutput.class, "getTimestamp", 20, 42l)
			.setup();

		long result = io.getTimestamp();

		assertThat(result, equalTo(42l));

		faked.verify();
	}

	@Test
	public void testOutputs() throws Exception {
		Outputs outputs = new Outputs();
		FakeIO faked = FakeIO.fake(Outputs.class)
			.fakeOutput(new Aspect() {
				public void print(String s) {
					return;
				}
			})
			.add(Outputs.class, "recorded", 13, null, equalTo("Hello "))
			.add(Outputs.class, "recorded", 14, null, equalTo("World"))
			.setup();

		outputs.recorded();

		faked.verify();
	}

	@Test
	public void testMissingOutputRecording() throws Exception {
		Outputs outputs = new Outputs();
		FakeIO faked = FakeIO.fake(Outputs.class)
			.fakeOutput(new Aspect() {
				public void print(String s) {
					return;
				}
			})
			.add(Outputs.class, "recorded", 13, null, equalTo("Hello "))
			.add(Outputs.class, "recorded", 14, null, equalTo("World"))
			.setup();

		Throwable exception = Throwables.capture(() -> outputs.notrecorded());

		assertThat(exception.getMessage(), containsPattern("missing input for:"
			+ "\n*called from*"
			+ "\n"
			+ "\nIf the input was recorded ensure that all call sites are recorded"));
		assertThat(Throwables.capture(faked::verify), matchesException(AssertionError.class)
			.withMessage(containsPattern("expected but not found"
				+ "*"
				+ "print(\"Hello \")"
				+ "*"
				+ "print(\"World\")")));
	}

	@Test
	public void testUnexpectedOutput() throws Exception {
		Outputs outputs = new Outputs();
		FakeIO faked = FakeIO.fake(Outputs.class)
			.fakeOutput(new Aspect() {
				public void print(String s) {
					return;
				}
			})
			.add(Outputs.class, "recorded", 13, null, equalTo("Hello "))
			.add(Outputs.class, "recorded", 14, null, equalTo("Welt"))
			.setup();

		Throwable exception = Throwables.capture(() -> outputs.recorded());

		assertThat(exception.getMessage(), equalTo("expected output:"
			+ "\nprint(\"Welt\")"
			+ "\nbut found:"
			+ "\nprint(\"World\")"));
		assertThat(Throwables.capture(faked::verify), matchesException(AssertionError.class)
			.withMessage(containsPattern("expected but not found"
				+ "* " + "print(\"Welt\")")));
	}

	@Test
	public void testMissingOutput() throws Exception {
		Outputs outputs = new Outputs();
		FakeIO faked = FakeIO.fake(Outputs.class)
			.fakeOutput(new Aspect() {
				public void print(String s) {
					return;
				}
			})
			.add(Outputs.class, "recorded", 13, null, equalTo("Hello "))
			.add(Outputs.class, "recorded", 14, null, equalTo("World"))
			.add(Outputs.class, "recorded", 15, null, equalTo("!"))
			.setup();

		assertThat(Throwables.capture(faked::verify), matchesException(AssertionError.class)
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
		FakeIO faked = FakeIO.fake(ByteArrayOutputStream.class)
			.fakeOutput(new Aspect() {
				public void write(byte[] value) {
					return;
				}
			})
			.add(StandardLibInputOutput.class, "store", 38, null, "My Output".getBytes())
			.setup();

		io.store("My Output");

		faked.verify();
	}

}
