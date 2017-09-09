package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExcludeStaticTest {

	@Test
	public void testTestTrueStatic() throws Exception {
		assertThat(new ExcludeStatic().matches(AnObject.class.getDeclaredField("staticStr")), is(true));
	}

	@Test
	public void testTestFalseIfNotStatic() throws Exception {
		assertThat(new ExcludeStatic().matches(AnObject.class.getDeclaredField("nonStaticStr")), is(false));
	}

	@SuppressWarnings("unused")
	public static class AnObject {
		private static String staticStr;
		private String nonStaticStr;

	}
}
