package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.FakeIO.AnyInvocation;
import net.amygdalum.testrecorder.FakeIO.Input;
import net.amygdalum.testrecorder.FakeIO.InvocationData;
import net.amygdalum.testrecorder.runtime.Invocation;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Static;

public class FakeIOInputTest {

	@Test
	public void testCallOnSameInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		Object[] args = new Object[2];
		InvocationData data = new InvocationData(any(), 1, new Object[2]);
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(1);
		assertThat(args).isEqualTo(new Object[2]);
	}

	@Test
	public void testCallNonSynchronizableNullInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		Object[] args = new Object[] { null, new Bean() };
		assertThatThrownBy(() -> {
			InvocationData data = new InvocationData(any(), null, new Object[] { new Bean(), new Bean() });
			input.call(data, args);
		}).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> {
			InvocationData data = new InvocationData(any(), null, new Object[] { null, null });
			input.call(data, args);
		}).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallOnEqualInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		Object[] args = new Object[] { "s1", "s2" };
		InvocationData data = new InvocationData(any(), 2, new Object[] { new String("s1"), new String("s2") });
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(2);
		assertThat(args).isEqualTo(new Object[] { "s1", "s2" });
	}

	@Test
	public void testCallNonEqualLiteralInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		Object[] args = new Object[] { "s1", "s2" };
		InvocationData data = new InvocationData(any(), null, new Object[] { new String("s1"), new String("s3") });

		assertThatThrownBy(() -> input.call(data, args)).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallOnSynchronizableInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		Simple[] args = new Simple[] { new Simple("s1"), new Simple("s2") };
		InvocationData data = new InvocationData(any(), 3, new Simple[] { new Simple("s3"), new Simple("s4") });
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(3);
		assertThat(args[0].getStr()).isEqualTo("s3");
		assertThat(args[1].getStr()).isEqualTo("s4");
	}

	@Test
	public void testCallOnSynchronizableArrayInputs() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		Object[] args = new Object[] { new byte[3] };
		InvocationData data = new InvocationData(any(), 4, new Object[] { "str".getBytes() });
		Object result = input.call(data, args);

		assertThat(result).isEqualTo(4);
		assertThat(args[0]).isEqualTo("str".getBytes());
	}

	@Test
	public void testCallVirtualBindableCallFailOnMismatchingInput() throws Exception {
		FakeIO fake = FakeIO.fake(Bean.class);
		Input input = new Input(fake, "getAttribute", "()Ljava/lang/String;");
		input.addVirtual(22, "expectedstr1", new Object[0]);
		input.addVirtual(22, "expectedstr2", new Object[0]);

		assertThatCode(() -> {
			input.call(Invocation.capture(new Bean(), Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);
			input.call(Invocation.capture(new Bean(), Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);
		})
			.hasMessageContaining("mismatching invocation Bean.getAttribute()")
			.isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallVirtualBindableCallFailOnSurplusInput() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		assertThatCode(() -> {
			input.call(Invocation.capture(new Bean(), Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);
		})
			.hasMessageContaining("surplus invocation Bean.getAttribute()")
			.isInstanceOf(AssertionError.class);
	}

	@Test
	public void testVerifySuccess() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");

		assertThatCode(() -> input.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testVerifyStaticCallSuccess() throws Exception {
		FakeIO fake = FakeIO.fake(Static.class);
		Input input = new Input(fake, "setGlobal", "(Ljava/lang/String;)V");
		input.addStatic("expectedstr", new Object[0]);

		Object result = input.call(Invocation.capture(null, Bean.class, "getGlobal", "()Ljava/lang/String;"), new Object[0]);

		assertThat(result).isEqualTo("expectedstr");
		assertThatCode(() -> input.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testVerifyStaticCallFail() throws Exception {
		Input input = new Input(FakeIO.fake(Static.class), "getGlobal", "()Ljava/lang/String;");
		input.addStatic("expectedstr", new Object[0]);

		assertThatCode(() -> input.verify())
			.hasMessageContaining("expected but not received call Static.getGlobal()")
			.isInstanceOf(AssertionError.class);
	}

	@Test
	public void testVerifyVirtualCallSuccess() throws Exception {
		FakeIO fake = FakeIO.fake(Bean.class);
		Input input = new Input(fake, "getAttribute", "()Ljava/lang/String;");
		Bean instance = new Bean();
		input.addVirtual(instance, "expectedstr", new Object[0]);

		Object result = input.call(Invocation.capture(instance, Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);

		assertThat(result).isEqualTo("expectedstr");
		assertThatCode(() -> input.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testVerifyVirtualCallFail() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");
		input.addVirtual(new Bean(), "expectedstr");

		assertThatCode(() -> input.verify())
			.hasMessageContaining("expected but not received call Bean.getAttribute()")
			.isInstanceOf(AssertionError.class);
	}

	@Test
	public void testVerifyVirtualBindableCallSuccessOneInstance() throws Exception {
		FakeIO fake = FakeIO.fake(Bean.class);
		Input input = new Input(fake, "getAttribute", "()Ljava/lang/String;");
		input.addFreeVirtual(22, "expectedstr1", new Object[0]);
		input.addFreeVirtual(22, "expectedstr2", new Object[0]);

		Bean instance = new Bean();
		Object result1 = input.call(Invocation.capture(instance, Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);
		Object result2 = input.call(Invocation.capture(instance, Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);

		assertThat(result1).isEqualTo("expectedstr1");
		assertThat(result2).isEqualTo("expectedstr2");
		assertThatCode(() -> input.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testVerifyVirtualBindableCallSuccessTwoInstances() throws Exception {
		FakeIO fake = FakeIO.fake(Bean.class);
		Input input = new Input(fake, "getAttribute", "()Ljava/lang/String;");
		input.addFreeVirtual(22, "expectedstr1", new Object[0]);
		input.addFreeVirtual(23, "expectedstr2", new Object[0]);

		Object result1 = input.call(Invocation.capture(new Bean(), Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);
		Object result2 = input.call(Invocation.capture(new Bean(), Bean.class, "getAttribute", "()Ljava/lang/String;"), new Object[0]);

		assertThat(result1).isEqualTo("expectedstr1");
		assertThat(result2).isEqualTo("expectedstr2");
		assertThatCode(() -> input.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testVerifyVirtualBindableCallFailOnMissingInput() throws Exception {
		Input input = new Input(FakeIO.fake(Bean.class), "getAttribute", "()Ljava/lang/String;");
		input.addVirtual(22, "expectedstr", new Object[0]);

		assertThatCode(() -> input.verify())
			.hasMessageContaining("expected but not received call Bean.getAttribute()")
			.isInstanceOf(AssertionError.class);
	}

	private AnyInvocation any() {
		return new FakeIO.AnyInvocation();
	}

}
