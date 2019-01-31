package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.extensions.assertj.conventions.DefaultEquality;

public class MethodSignatureTest {

	@Nested
	class testMethodSignature {
		@Test
		void failingOnDeclaringClassNull() throws Exception {
			assertThatThrownBy(() -> new MethodSignature(null, String.class, "method", new Type[] {int.class}))
				.isInstanceOf(AssertionError.class);
		}

		@Test
		void failingOnResultTypeNull() throws Exception {
			assertThatThrownBy(() -> new MethodSignature(MyObject.class, null, "method", new Type[] {int.class}))
				.isInstanceOf(AssertionError.class);
		}

		@Test
		void failingOnMethodNameNull() throws Exception {
			assertThatThrownBy(() -> new MethodSignature(MyObject.class, String.class, null, new Type[] {int.class}))
				.isInstanceOf(AssertionError.class);
		}

		@Test
		void failingOnParameterTypesNull() throws Exception {
			assertThatThrownBy(() -> new MethodSignature(MyObject.class, String.class, "method", null))
				.isInstanceOf(AssertionError.class);
		}
	}

	@Test
	void testSerializable() throws Exception {
		MethodSignature signature = new MethodSignature(MyObject.class, String.class, "method", new Type[] {int.class});

		MethodSignature deserialized = new TestDeSerializer().deSerialize(signature);

		assertThat(deserialized).isEqualTo(signature);
	}

	@Test
	void testResolveMethod() throws Exception {
		MethodSignature signature = new MethodSignature(MyObject.class, String.class, "method", new Type[] {int.class});

		Method method = signature.resolveMethod();
		assertThat(method.getDeclaringClass()).isSameAs(MyObject.class);
		assertThat(method.getName()).isEqualTo("method");
		assertThat(method.getParameterTypes()).contains(int.class);
		assertThat(method.getReturnType()).isEqualTo(String.class);
	}

	@Test
	void testToString() throws Exception {
		MethodSignature signature = new MethodSignature(MyObject.class, String.class, "method", new Type[] {int.class});
		assertThat(signature.toString()).isEqualTo("java.lang.String method(int) of net.amygdalum.testrecorder.types.MethodSignatureTest$MyObject");
	}

	@Test
	void testEquals() throws Exception {
		MethodSignature signature = new MethodSignature(MyObject.class, String.class, "method", new Type[] {int.class});
		assertThat(signature).satisfies(DefaultEquality.defaultEquality()
			.andEqualTo(new MethodSignature(MyObject.class, String.class, "method", new Type[] {int.class}))
			.andNotEqualTo(new MethodSignature(Object.class, String.class, "method", new Type[] {int.class}))
			.andNotEqualTo(new MethodSignature(MyObject.class, Object.class, "method", new Type[] {int.class}))
			.andNotEqualTo(new MethodSignature(MyObject.class, String.class, "other", new Type[] {int.class}))
			.andNotEqualTo(new MethodSignature(MyObject.class, String.class, "method", new Type[] {long.class}))
			.conventions());
	}

	public static class MyObject {
		public String method(int i) {
			return "" + i;
		}
	}

}
