package net.amygdalum.testrecorder.ioscenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.FakeIO;
import net.amygdalum.testrecorder.runtime.Aspect;
import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;

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
			.addVirtual(inputs, "Hello")
			.addVirtual(inputs, " ")
			.addVirtual(inputs, "World")
			.setup();

		String result = inputs.recorded();

		assertThat(result).isEqualTo("Hello World");
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
			.addVirtual(inputs, true)
			.fakeInput(new Aspect() {
				public byte readByte() {
					return 0;
				}
			})
			.addVirtual(inputs, (byte) 2)
			.fakeInput(new Aspect() {
				public short readShort() {
					return 0;
				}
			})
			.addVirtual(inputs, (short) 3)
			.fakeInput(new Aspect() {
				public int readInt() {
					return 0;
				}
			})
			.addVirtual(inputs, 4)
			.fakeInput(new Aspect() {
				public long readLong() {
					return 0;
				}
			})
			.addVirtual(inputs, 5l)
			.fakeInput(new Aspect() {
				public float readFloat() {
					return 0;
				}
			})
			.addVirtual(inputs, 6f)
			.fakeInput(new Aspect() {
				public double readDouble() {
					return 0;
				}
			})
			.addVirtual(inputs, 7d)
			.fakeInput(new Aspect() {
				public char readChar() {
					return 0;
				}
			})
			.addVirtual(inputs, 'x')
			.setup();

		String result = inputs.primitivesRecorded();

		assertThat(result).isEqualTo(""
			+ "boolean:true"
			+ "byte:2"
			+ "short:3"
			+ "int:4"
			+ "long:5"
			+ "float:6.0"
			+ "double:7.0"
			+ "char:x");
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
			.addVirtual(inputs, null, "Hello World".toCharArray())
			.setup();

		String result = inputs.sideEffectsRecorded();

		assertThat(result).isEqualTo("Hello World");
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
			.addVirtual(inputs, null, list)
			.setup();

		String result = inputs.objectSideEffectsRecorded();

		assertThat(result).isEqualTo("[Hello, World]");
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
			.addVirtual(inputs, "Hello")
			.addVirtual(inputs, " ")
			.addVirtual(inputs, "World")
			.setup();

		assertThatThrownBy(faked::verify)
			.isInstanceOf(AssertionError.class)
			.satisfies(e -> assertThat(e.getMessage()).containsWildcardPattern("expected but not found"
				+ "*"
				+ "read()"
				+ "*"
				+ "read()"
				+ "*"
				+ "read()"));
	}

	@Test
	public void testSurplusInputRequest() throws Exception {
		Inputs inputs = new Inputs();
		FakeIO faked = FakeIO.fake(Inputs.class)
			.fakeInput(new Aspect() {
				public String read() {
					return null;
				}
			})
			.addVirtual(inputs, "Hello")
			.addVirtual(inputs, "")
			.setup();

		Throwable exception = Throwables.capture(() -> inputs.recorded());

		assertThat(exception.getMessage()).containsWildcardPattern("surplus invocation Inputs.read()"
			+ "\n"
			+ "\nIf the input was recorded ensure that all call sites are recorded");
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
			.addStatic(42l)
			.setup();

		long result = io.getTimestamp();

		assertThat(result).isEqualTo(42l);

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
			.addVirtual(outputs, null, equalTo("Hello "))
			.addVirtual(outputs, null, equalTo("World"))
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
			.addVirtual(outputs, null, equalTo("Hello "))
			.addVirtual(outputs, null, equalTo("World"))
			.setup();

		assertThatThrownBy(faked::verify)
			.isInstanceOf(AssertionError.class)
			.satisfies(e -> assertThat(e.getMessage())
				.containsWildcardPattern("expected but not found"
					+ "*"
					+ "print(\"Hello \")"
					+ "*"
					+ "print(\"World\")"));
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
			.addVirtual(outputs, null, equalTo("Hello "))
			.addVirtual(outputs, null, equalTo("Welt"))
			.setup();

		Throwable exception = Throwables.capture(() -> outputs.recorded());

		assertThat(exception.getMessage()).isEqualTo("expected output:"
			+ "\nprint(\"Welt\")"
			+ "\nbut found:"
			+ "\nprint(\"World\")");
		assertThatThrownBy(faked::verify)
			.isInstanceOf(AssertionError.class)
			.satisfies(e -> assertThat(e.getMessage()).containsWildcardPattern("expected but not found"
				+ "* " + "print(\"Welt\")"));
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
			.addVirtual(outputs, null, equalTo("Hello "))
			.addVirtual(outputs, null, equalTo("World"))
			.addVirtual(outputs, null, equalTo("!"))
			.setup();

		assertThatThrownBy(faked::verify)
			.isInstanceOf(AssertionError.class)
			.satisfies(e -> assertThat(e.getMessage()).containsWildcardPattern("expected but not found"
				+ "*"
				+ "print(\"Hello \")"
				+ "*"
				+ "print(\"World\")"
				+ "*"
				+ "print(\"!\")"));
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
			.addFreeVirtual(22, null, new Object[] {"My Output".getBytes()})
			.setup();

		io.store("My Output");

		assertThatCode(() -> faked.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testCall() throws Exception {
		FakeIO faked = FakeIO.fake(Bean.class);
		assertThat(faked.call(null)).isSameAs(FakeIO.NO_RESULT);
	}

	@Test
	public void testMatches() throws Exception {
		FakeIO faked = FakeIO.fake(Super.class);

		assertThat(faked.matches(new Super(), Super.class)).isTrue();
		assertThat(faked.matches(new Sub(), Object.class)).isTrue();
		assertThat(faked.matches(new Object(), Super.class)).isTrue();
		assertThat(faked.matches(new Object(), Object.class)).isFalse();
	}

	@Test
	public void testCallFakeNonRecording() throws Exception {
		Object result = FakeIO.callFake("name", new Object(), "methodName", "methodDesc");
		
		assertThat(result).isSameAs(FakeIO.NO_RESULT);
	}

	@Test
	public void testCallFakeNotFaked() throws Exception {
		Object result = FakeIO.callFake("name", new Object(), "methodName", "methodDesc");
		
		assertThat(result).isSameAs(FakeIO.NO_RESULT);
	}

}
