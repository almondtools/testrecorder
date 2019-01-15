package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExcludeExplicitExcludedTest {

	@Nested
	class testMatches {
		@Test
		public void trueIfContainingDollar() throws Exception {
			assertThat(new ExcludeExplicitExcluded().matches(AnObject.class.getDeclaredField("excluded"))).isTrue();
		}

		@Test
		public void falseIfNotContainingDollar() throws Exception {
			assertThat(new ExcludeExplicitExcluded().matches(AnObject.class.getDeclaredField("included"))).isFalse();
		}
	}

	@SuppressWarnings("unused")
	public static class AnObject {
		@Excluded
		private String excluded;
		private String included;

	}
}
