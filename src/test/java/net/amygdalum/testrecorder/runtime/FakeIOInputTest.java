package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.runtime.FakeIO.Input;
import net.amygdalum.testrecorder.runtime.FakeIO.InvocationData;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class FakeIOInputTest {

	@Test
	public void testCallOnSameInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		Object[] args = new Object[2];
		InvocationData data = new InvocationData(FakeIOInputTest.class, "testCall", 0, 1, new Object[2]);
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(1);
		assertThat(args).isEqualTo(new Object[2]);
	}

	@Test
	public void testCallNonSynchronizableNullInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		Object[] args = new Object[] { null, new Bean() };
		assertThatThrownBy(() -> {
			InvocationData data = new InvocationData(FakeIOInputTest.class, "testCall", 0, null, new Object[] { new Bean(), new Bean() });
			input.call(data, args);
		}).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> {
			InvocationData data = new InvocationData(FakeIOInputTest.class, "testCall", 0, null, new Object[] { null, null });
			input.call(data, args);
		}).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallOnEqualInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		Object[] args = new Object[] { "s1", "s2" };
		InvocationData data = new InvocationData(FakeIOInputTest.class, "testCall", 0, 2, new Object[] { new String("s1"), new String("s2") });
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(2);
		assertThat(args).isEqualTo(new Object[] { "s1", "s2" });
	}

	@Test
	public void testCallNonEqualLiteralInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		Object[] args = new Object[] { "s1", "s2" };
		InvocationData data = new InvocationData(FakeIOInputTest.class, "testCall", 0, null, new Object[] { new String("s1"), new String("s3") });

		assertThatThrownBy(() -> input.call(data, args)).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallOnSynchronizableInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		Simple[] args = new Simple[] { new Simple("s1"), new Simple("s2") };
		InvocationData data = new InvocationData(FakeIOInputTest.class, "testCall", 0, 3, new Simple[] { new Simple("s3"), new Simple("s4") });
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(3);
		assertThat(args[0].getStr()).isEqualTo("s3");
		assertThat(args[1].getStr()).isEqualTo("s4");
	}

	@Test
	public void testCallOnSynchronizableArrayInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		Object[] args = new Object[] { new byte[3] };
		InvocationData data = new InvocationData(FakeIOInputTest.class, "testCall", 0, 4, new Object[] { "str".getBytes() });
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(4);
		assertThat(args[0]).isEqualTo("str".getBytes());
	}

	@Test
	public void testVerifySuccess() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		assertThatCode(() -> input.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testVerifyFail() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");
		input.add(FakeIOInputTest.class, "testCall", 0, null, new Object[] { "expectedstr" });

		assertThatCode(() -> input.verify())
			.hasMessageContaining("expected but not received call setAttribute(\"expectedstr\")")
			.isInstanceOf(AssertionError.class);
	}
}
