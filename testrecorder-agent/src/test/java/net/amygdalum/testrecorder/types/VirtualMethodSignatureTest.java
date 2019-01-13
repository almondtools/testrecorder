package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;

public class VirtualMethodSignatureTest {

	@Test
	void testNULL() throws Exception {
		assertThat(VirtualMethodSignature.NULL.validIn(Object.class)).isFalse();
	}

	@Nested
	class testValidIn {
		
		@Test
		void onClass() throws Exception {
			Method method = Simple.class.getDeclaredMethod("getStr");

			assertThat(VirtualMethodSignature.fromDescriptor(method).validIn(Simple.class)).isTrue();
			assertThat(VirtualMethodSignature.fromDescriptor(method).validIn(Object.class)).isFalse();
			assertThat(VirtualMethodSignature.fromDescriptor(method).validIn(Complex.class)).isFalse();
			assertThat(VirtualMethodSignature.NULL.validIn(Object.class)).isFalse();
		}

		@Test
		void onInheritedClass() throws Exception {
			Method method = Super.class.getDeclaredMethod("getStr");

			assertThat(VirtualMethodSignature.fromDescriptor(method).validIn(Super.class)).isTrue();
			assertThat(VirtualMethodSignature.fromDescriptor(method).validIn(Sub.class)).isTrue();
			assertThat(VirtualMethodSignature.fromDescriptor(method).validIn(Simple.class)).isFalse();
			assertThat(VirtualMethodSignature.fromDescriptor(method).validIn(Object.class)).isFalse();
		}

		@Test
		void withCached() throws Exception {
			Method method = Super.class.getDeclaredMethod("getStr");
			VirtualMethodSignature descriptor = VirtualMethodSignature.fromDescriptor(method);

			assertThat(descriptor.validIn(Sub.class)).isTrue();
			assertThat(descriptor.validIn(Sub.class)).isTrue();
		}

		@Test
		void withInvalidCached() throws Exception {
			Method method = Super.class.getDeclaredMethod("getStr");
			VirtualMethodSignature descriptor = VirtualMethodSignature.fromDescriptor(method);

			assertThat(descriptor.validIn(Simple.class)).isFalse();
			assertThat(descriptor.validIn(Simple.class)).isFalse();
		}
	}
}
