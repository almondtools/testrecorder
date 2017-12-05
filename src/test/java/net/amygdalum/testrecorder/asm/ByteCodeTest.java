package net.amygdalum.testrecorder.asm;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.conmatch.conventions.UtilityClassMatcher;

public class ByteCodeTest {

	@Test
	public void testByteCode() throws Exception {
		assertThat(ByteCode.class, UtilityClassMatcher.isUtilityClass());
	}

	@Test
	public void testConstructorDescriptor() throws Exception {
		assertThat(ByteCode.constructorDescriptor(String.class), equalTo("()V"));
		assertThat(ByteCode.constructorDescriptor(String.class, char[].class), equalTo("([C)V"));
	}

	@Test
	public void testMethodDescriptor() throws Exception {
		assertThat(ByteCode.methodDescriptor(String.class, "getBytes"), equalTo("()[B"));
		assertThat(ByteCode.methodDescriptor(String.class, "valueOf", char[].class), equalTo("([C)Ljava/lang/String;"));
	}

}
