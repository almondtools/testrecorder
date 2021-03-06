package net.amygdalum.testrecorder.fakeio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.doReturn;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.fakeio.FakeIO.Input;
import net.amygdalum.testrecorder.fakeio.FakeIO.InvocationData;
import net.amygdalum.testrecorder.fakeio.FakeIO.Output;
import net.amygdalum.testrecorder.runtime.Aspect;
import net.amygdalum.testrecorder.runtime.Invocation;
import net.amygdalum.testrecorder.util.testobjects.Abstract;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Implementor;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;

public class FakeIOInteractionTest {

	@Nested
	class testResolve {
		@Test
		void staticCase() throws Exception {
			MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

			assertThat(myInteraction.resolve(Bean.class)).isSameAs(Bean.class);
		}

		@Test
		void inheritedCase() throws Exception {
			MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Sub.class), "getStr", "()Ljava/lang/String;");

			assertThat(myInteraction.resolve(Sub.class)).isSameAs(Super.class);
		}

		@Test
		void abstractCase() throws Exception {
			MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Abstract.class), "getAbstract", "()Ljava/lang/String;");

			assertThat(myInteraction.resolve(Implementor.class)).isSameAs(Implementor.class);
		}

		@Test
		void brokenName() throws Exception {
			MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Object.class), "notExistingMethod", "()V");

			assertThatThrownBy(() -> myInteraction.resolve(Object.class)).isInstanceOf(RuntimeException.class);
		}
	}

	@Nested
	class testMatches {
		@Test
		void ifFakeAndInteractionMatch() throws Exception {
			MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

			assertThat(myInteraction.matches(Invocation.capture(new Bean(), Bean.class, "setAttribute", "(Ljava/lang/String;)V"))).isTrue();
			assertThat(myInteraction.matches(Invocation.capture(new Bean(), Bean.class, "setOtherAttribute", "(Ljava/lang/String;)V"))).isFalse();
			assertThat(myInteraction.matches(Invocation.capture(new Object(), Object.class, "setAttribute", "(Ljava/lang/String;)V"))).isFalse();
			assertThat(myInteraction.matches(Invocation.capture(new Bean(), Bean.class, "setAttribute", "(I)V"))).isFalse();
		}
	}

	@Test
	void testCallMatching() throws Exception {
		Object RESULT = new Object();

		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");
		Bean instance = new Bean();
		myInteraction.addVirtual(instance, null, new Object[] {"mystr"});
		myInteraction.setResult(RESULT);

		Object result = myInteraction.call(Invocation.capture(instance, Bean.class, "setAttribute", "(Ljava/lang/String;)V"), new Object[] {"mystr"});

		assertThat(result).isSameAs(RESULT);
	}

	@Test
	void testGetMethod() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		assertThat(myInteraction.getMethod()).isEqualTo("setAttribute(Ljava/lang/String;)V");
	}

	@Test
	void testFakeInput() throws Exception {
		FakeIO fakeIO = Mockito.mock(FakeIO.class);
		doReturn(new Input(fakeIO, "fakedInput", "()V")).when(fakeIO).fakeInput(Mockito.any(Aspect.class));

		MyInteraction myInteraction = new MyInteraction(fakeIO, "setAttribute", "(Ljava/lang/String;)V");

		assertThat(myInteraction.fakeInput(new Aspect() {
			@SuppressWarnings("unused")
			public void fakedInput() {
			}
		}).getMethod()).isEqualTo("fakedInput()V");
	}

	@Test
	void testFakeOutput() throws Exception {
		FakeIO fakeIO = Mockito.mock(FakeIO.class);
		doReturn(new Output(fakeIO, "fakedOutput", "()V")).when(fakeIO).fakeOutput(Mockito.any(Aspect.class));

		MyInteraction myInteraction = new MyInteraction(fakeIO, "setAttribute", "(Ljava/lang/String;)V");

		assertThat(myInteraction.fakeOutput(new Aspect() {
			@SuppressWarnings("unused")
			public void fakedOutput() {
			}
		}).getMethod()).isEqualTo("fakedOutput()V");
	}

	@Test
	void testSetup() throws Exception {
		FakeIO fakeIO = Mockito.mock(FakeIO.class);
		doReturn(fakeIO).when(fakeIO).setup();

		MyInteraction myInteraction = new MyInteraction(fakeIO, "setAttribute", "(Ljava/lang/String;)V");

		assertThat(myInteraction.setup()).isSameAs(fakeIO);
	}

	@Test
	void testSignatureFor() throws Exception {
		FakeIO fakeIO = Mockito.mock(FakeIO.class);
		doReturn(fakeIO).when(fakeIO).setup();

		MyInteraction myInteraction = new MyInteraction(fakeIO, "setAttribute", "(Ljava/lang/String;)V");

		assertThat(myInteraction.signatureFor(new Object[] {"str", Long.valueOf(4), Byte.valueOf((byte) 1)})).isEqualTo("setAttribute(\"str\", <4L>, <1b>)");
	}

	@Test
	void testSignatureForMatchers() throws Exception {
		FakeIO fakeIO = Mockito.mock(FakeIO.class);
		doReturn(fakeIO).when(fakeIO).setup();

		MyInteraction myInteraction = new MyInteraction(fakeIO, "setAttribute", "(Ljava/lang/String;)V");

		assertThat(myInteraction.signatureFor(new Object[] {"str", notNullValue(), instanceOf(List.class)})).isEqualTo("setAttribute(\"str\", not null, an instance of java.util.List)");
	}

	private static class MyInteraction extends FakeIO.Interaction {

		private Object result;

		MyInteraction(FakeIO io, String methodName, String methodDesc) {
			super(io, methodName, methodDesc);
		}

		public void setResult(Object result) {
			this.result = result;
		}

		@Override
		public Object call(InvocationData data, Object[] arguments) {
			return result;
		}

		@Override
		public void verify() {
		}

	}

}
