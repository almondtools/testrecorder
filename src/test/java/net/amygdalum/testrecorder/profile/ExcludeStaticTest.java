package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ExcludeStaticTest {

	@Test
	public void testTestTrueStatic() throws Exception {
		assertThat(new ExcludeStatic().matches(AnObject.class.getDeclaredField("staticStr"))).isTrue();
	}

	@Test
	public void testTestFalseIfNotStatic() throws Exception {
		assertThat(new ExcludeStatic().matches(AnObject.class.getDeclaredField("nonStaticStr"))).isFalse();
	}

	@SuppressWarnings("unused")
	public static class AnObject {
		private static String staticStr;
		private String nonStaticStr;

	}
}
