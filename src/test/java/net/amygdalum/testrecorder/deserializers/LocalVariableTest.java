package net.amygdalum.testrecorder.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LocalVariableTest {

	@Test
	public void testLocalVariableIsAllocated() throws Exception {
		LocalVariable i = new LocalVariable("i");
		assertThat(i.isDefined()).isFalse();
		assertThat(i.isReady()).isFalse();

		LocalVariable iTyped = new LocalVariable("i", Integer.class);
		assertThat(iTyped.isDefined()).isFalse();
		assertThat(iTyped.isReady()).isFalse();
	}

	@Test
	public void testLocalVariableAfterDefine() throws Exception {
		LocalVariable define = new LocalVariable("i")
			.define(Integer.class);
		assertThat(define.isDefined()).isTrue();
		assertThat(define.isReady()).isFalse();
	}

	@Test
	public void testLocalVariableAfterFinish() throws Exception {
		LocalVariable define = new LocalVariable("i")
			.define(Integer.class)
			.finish();
		assertThat(define.isDefined()).isTrue();
		assertThat(define.isReady()).isTrue();
	}

}
