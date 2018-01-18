package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.runtime.FakeIO.InvocationData;
import net.amygdalum.testrecorder.util.testobjects.Abstract;
import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Implementor;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;

public class FakeIOInteractionTest {

	@Test
	public void testResolveStaticCase() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		assertThat(myInteraction.resolve(Bean.class)).isSameAs(Bean.class);
	}

	@Test
	public void testResolveInheritedCase() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Sub.class), "getStr", "()Ljava/lang/String;");

		assertThat(myInteraction.resolve(Sub.class)).isSameAs(Super.class);
	}

	@Test
	public void testResolveAbstractCase() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Abstract.class), "getAbstract", "()Ljava/lang/String;");

		assertThat(myInteraction.resolve(Implementor.class)).isSameAs(Implementor.class);
	}

	@Test
	public void testResolveBrokenName() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Object.class), "notExistingMethod", "()V");

		assertThatThrownBy(() -> myInteraction.resolve(Object.class)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void testMatchesIfFakeAndInteractionMatch() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");

		StackTraceElement[] stackTrace = new StackTraceElement[] {
			new StackTraceElement("net.amygdalum.testrecorder.util.testobjects.Bean", "setAttribute", "Bean.java", 0),
			new StackTraceElement("net.amygdalum.testrecorder.runtime.FakeIOInteractionTest", "testMatchesIfFakeAndInteractionMatch", "FakeIOInteractionTest.java", 0)
		};
		assertThat(myInteraction.matches(Invocation.capture(stackTrace, new Bean(), Bean.class, "setAttribute", "(Ljava/lang/String;)V"))).isTrue();
		assertThat(myInteraction.matches(Invocation.capture(stackTrace, new Bean(), Bean.class, "setOtherAttribute", "(Ljava/lang/String;)V"))).isFalse();
		assertThat(myInteraction.matches(Invocation.capture(stackTrace, new Object(), Object.class, "setAttribute", "(Ljava/lang/String;)V"))).isFalse();
		assertThat(myInteraction.matches(Invocation.capture(stackTrace, new Bean(), Bean.class, "setAttribute", "(I)V"))).isFalse();
	}

	@Test
	void testCallFilteringRuntimeClasses() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");
		myInteraction.add(FakeIOInteractionTest.class, "testCallFiltered", 0, null);

		StackTraceElement[] stackTrace = new StackTraceElement[] {
			new StackTraceElement("net.amygdalum.testrecorder.util.testobjects.Bean", "setAttribute", "Bean.java", 0),
			new StackTraceElement("net.amygdalum.testrecorder.runtime.FakeIO", "call", "FakeIO.java", 0)
		};
		Object result = myInteraction.call(Invocation.capture(stackTrace, new Bean(), Bean.class, "setAttribute", "(Ljava/lang/String;)V"), new Object[] { "mystr" });

		assertThat(result).isNull();
	}

	@Test
	void testCallFilteringTestingClasses() throws Exception {
		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");
		myInteraction.add(FakeIOInteractionTest.class, "testCallFiltered", 0, null);

		StackTraceElement[] stackTrace = new StackTraceElement[] {
			new StackTraceElement("net.amygdalum.testrecorder.util.testobjects.Bean", "setAttribute", "Bean.java", 0),
			new StackTraceElement("net.amygdalum.testrecorder.testing.hamcrest.GenericMatcher", "matches", "GenericMatcher.java", 0)
		};
		Object result = myInteraction.call(Invocation.capture(stackTrace, new Bean(), Bean.class, "setAttribute", "(Ljava/lang/String;)V"), new Object[] { "mystr" });

		assertThat(result).isNull();
	}

	@Test
	void testCallMatching() throws Exception {
		Object RESULT = new Object();

		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");
		myInteraction.add(Bean.class, "setAttribute", 0, null);
		myInteraction.setResult(RESULT);

		StackTraceElement[] stackTrace = new StackTraceElement[] {
			new StackTraceElement("net.amygdalum.testrecorder.util.testobjects.Bean", "setAttribute", "Bean.java", 0),
			new StackTraceElement("net.amygdalum.testrecorder.util.testobjects.Bean", "setAttribute", "Bean.java", 0)
		};
		Object result = myInteraction.call(Invocation.capture(stackTrace, new Bean(), Bean.class, "setAttribute", "(Ljava/lang/String;)V"), new Object[] { "mystr" });

		assertThat(result).isSameAs(RESULT);
	}

	@Test
	void testCallFailing() throws Exception {
		Object RESULT = new Object();

		MyInteraction myInteraction = new MyInteraction(FakeIO.fake(Bean.class), "setAttribute", "(Ljava/lang/String;)V");
		myInteraction.add(Bean.class, "setAttribute", 0, null);
		myInteraction.setResult(RESULT);

		StackTraceElement[] stackTrace = new StackTraceElement[] {
			new StackTraceElement("net.amygdalum.testrecorder.util.testobjects.Bean", "setAttribute", "Bean.java", 0),
			new StackTraceElement("net.amygdalum.testrecorder.util.testobjects.Simple", "setAttribute", "Bean.java", 0)
		};
		assertThatThrownBy(() -> myInteraction.call(Invocation.capture(stackTrace, new Bean(), Bean.class, "setAttribute", "(Ljava/lang/String;)V"), new Object[] { "mystr" }))
			.isInstanceOf(AssertionError.class);

	}

	private static class MyInteraction extends FakeIO.Interaction {

		private Object result;

		public MyInteraction(FakeIO io, String methodName, String methodDesc) {
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
