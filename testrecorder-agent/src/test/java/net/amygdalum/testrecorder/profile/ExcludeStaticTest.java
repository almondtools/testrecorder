package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExcludeStaticTest {

	@Nested
	class testMatches {
		@Test
		public void trueStatic() throws Exception {
			assertThat(new ExcludeStatic().matches(AnObject.class.getDeclaredField("staticStr"))).isTrue();
		}

		@Test
		public void falseIfNotStatic() throws Exception {
			assertThat(new ExcludeStatic().matches(AnObject.class.getDeclaredField("nonStaticStr"))).isFalse();
		}
	}

	@SuppressWarnings("unused")
	public static class AnObject {
		private static String staticStr;
		private String nonStaticStr;

	}
}
