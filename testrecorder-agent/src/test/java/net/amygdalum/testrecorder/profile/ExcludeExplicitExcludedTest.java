package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.profile.SerializationProfile.Excluded;

public class ExcludeExplicitExcludedTest {

	@Test
	public void testTestTrueIfContainingDollar() throws Exception {
		assertThat(new ExcludeExplicitExcluded().matches(AnObject.class.getDeclaredField("excluded"))).isTrue();
	}

	@Test
	public void testTestFalseIfNotContainingDollar() throws Exception {
		assertThat(new ExcludeExplicitExcluded().matches(AnObject.class.getDeclaredField("included"))).isFalse();
	}


	@SuppressWarnings("unused")
	public static class AnObject {
		@Excluded
		private String excluded;
		private String included;

	}
}
