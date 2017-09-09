package net.amygdalum.testrecorder.deserializers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class LocalVariableTest {

	@Test
	public void testLocalVariableIsAllocated() throws Exception {
		LocalVariable i = new LocalVariable("i");
		assertThat(i.isDefined(), is(false));
		assertThat(i.isReady(), is(false));

		LocalVariable iTyped = new LocalVariable("i", Integer.class);
		assertThat(iTyped.isDefined(), is(false));
		assertThat(iTyped.isReady(), is(false));
	}

	@Test
	public void testLocalVariableAfterDefine() throws Exception {
		LocalVariable define = new LocalVariable("i")
			.define(Integer.class);
		assertThat(define.isDefined(), is(true));
		assertThat(define.isReady(), is(false));
	}

	@Test
	public void testLocalVariableAfterFinish() throws Exception {
		LocalVariable define = new LocalVariable("i")
			.define(Integer.class)
			.finish();
		assertThat(define.isDefined(), is(true));
		assertThat(define.isReady(), is(true));
	}

}
