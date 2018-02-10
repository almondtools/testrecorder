package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.FakeIO;
import net.amygdalum.testrecorder.FakeIO.InvocationData;
import net.amygdalum.testrecorder.FakeIO.Output;
import net.amygdalum.testrecorder.util.testobjects.Bean;

public class FakeIOOutputTest {

	@Test
	public void testCallOnSameOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(1, new Object[2]);
		Object result = output.call(data, new Object[2]);

		assertThat(result).isEqualTo(1);
	}

	@Test
	public void testCallNonSynchronizableNullOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		assertThatThrownBy(() -> {
			InvocationData data = new InvocationData(null, new Object[] { new Bean(), new Bean() });
			output.call(data, new Object[] { null, new Bean() });
		}).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> {
			InvocationData data = new InvocationData(null, new Object[] { null, null });
			output.call(data, new Object[] { null, new Bean() });
		}).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallOnEqualOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(2, new Object[] { new String("s1"), new String("s2") });
		Object result = output.call(data, new Object[] { "s1", "s2" });

		assertThat(result).isEqualTo(2);
		assertThat(new Object[] { "s1", "s2" }).isEqualTo(new Object[] { "s1", "s2" });
	}

	@Test
	public void testCallNonEqualLiteralOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(null, new Object[] { new String("s1"), new String("s3") });

		assertThatThrownBy(() -> output.call(data, new Object[] { "s1", "s2" })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallMatcherOutputsMatching() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { equalTo("s1") });

		assertThat(output.call(data, new Object[] { "s1" })).isEqualTo(3);
	}

	@Test
	public void testCallMatcherOutputsFailing() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { equalTo("s2") });

		assertThatThrownBy(() -> output.call(data, new Object[] { "s1" })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallBooleanArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new boolean[] { true } });

		assertThat(output.call(data, new Object[] { new boolean[] { true } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new boolean[] { false } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallByteArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new byte[] { 2 } });

		assertThat(output.call(data, new Object[] { new byte[] { 2 } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new byte[] { 3 } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallShortArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new short[] { 2 } });

		assertThat(output.call(data, new Object[] { new short[] { 2 } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new short[] { 3 } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallIntArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new int[] { 2 } });

		assertThat(output.call(data, new Object[] { new int[] { 2 } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new int[] { 3 } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallLongArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new long[] { 2 } });

		assertThat(output.call(data, new Object[] { new long[] { 2 } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new long[] { 3 } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallFloatArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new float[] { 2 } });

		assertThat(output.call(data, new Object[] { new float[] { 2 } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new float[] { 3 } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallDoubleArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new double[] { 2 } });

		assertThat(output.call(data, new Object[] { new double[] { 2 } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new double[] { 3 } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallCharArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { "chars".toCharArray() });

		assertThat(output.call(data, new Object[] { "chars".toCharArray() })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { "otherchars".toCharArray() })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testCallObjectArrayOutputs() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		InvocationData data = new InvocationData(3, new Object[] { new Object[] { "string" } });

		assertThat(output.call(data, new Object[] { new Object[] { "string" } })).isEqualTo(3);
		assertThat(output.call(data, new Object[] { new String[] { "string" } })).isEqualTo(3);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object[] { "otherString" } })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { null })).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() -> output.call(data, new Object[] { new Object() })).isInstanceOf(AssertionError.class);
	}

	@Test
	public void testVerifySuccess() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		assertThatCode(() -> output.verify()).doesNotThrowAnyException();
	}

	@Test
	public void testVerifyFail() throws Exception {
		Output output = new Output(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");
		output.add(null, new Object[] { "expectedstr" });

		assertThatCode(() -> output.verify())
			.hasMessageContaining("expected but not received call setAttribute(\"expectedstr\")")
			.isInstanceOf(AssertionError.class);
	}
}
