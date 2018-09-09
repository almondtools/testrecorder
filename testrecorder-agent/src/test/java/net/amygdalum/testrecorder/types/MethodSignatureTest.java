package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;


public class MethodSignatureTest {

	@Test
	void testNULL() throws Exception {
		assertThat(MethodSignature.NULL.validIn(Object.class)).isFalse();
	}

	@Test
	public void testValidIn() throws Exception {
		Method method = Simple.class.getDeclaredMethod("getStr");

		assertThat(MethodSignature.fromDescriptor(method).validIn(Simple.class)).isTrue();
		assertThat(MethodSignature.fromDescriptor(method).validIn(Object.class)).isFalse();
		assertThat(MethodSignature.fromDescriptor(method).validIn(Complex.class)).isFalse();
	}

	@Test
	public void testValidInInherited() throws Exception {
		Method method = Super.class.getDeclaredMethod("getStr");
		
		assertThat(MethodSignature.fromDescriptor(method).validIn(Super.class)).isTrue();
		assertThat(MethodSignature.fromDescriptor(method).validIn(Sub.class)).isTrue();
		assertThat(MethodSignature.fromDescriptor(method).validIn(Simple.class)).isFalse();
		assertThat(MethodSignature.fromDescriptor(method).validIn(Object.class)).isFalse();
	}

	@Test
	public void testValidInWithValidCached() throws Exception {
		Method method = Super.class.getDeclaredMethod("getStr");
		MethodSignature descriptor = MethodSignature.fromDescriptor(method);
		
		assertThat(descriptor.validIn(Sub.class)).isTrue();
		assertThat(descriptor.validIn(Sub.class)).isTrue();
	}

	@Test
	public void testValidInWithInvalidCached() throws Exception {
		Method method = Super.class.getDeclaredMethod("getStr");
		MethodSignature descriptor = MethodSignature.fromDescriptor(method);
		
		assertThat(descriptor.validIn(Simple.class)).isFalse();
		assertThat(descriptor.validIn(Simple.class)).isFalse();
	}

}
