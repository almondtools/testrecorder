package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExcludeGeneratedTest {

	@Nested
	class testMatches {
		@Test
		public void trueIfContainingDollar() throws Exception {
			assertThat(new ExcludeGenerated().matches(AnObject.class.getDeclaredField("$generated"))).isTrue();
		}

		@Test
		public void falseIfNotContainingDollar() throws Exception {
			assertThat(new ExcludeGenerated().matches(AnObject.class.getDeclaredField("notgenerated"))).isFalse();
		}
	}

	@SuppressWarnings("unused")
	public static class AnObject {
		private String $generated;
		private String notgenerated;

	}
}
