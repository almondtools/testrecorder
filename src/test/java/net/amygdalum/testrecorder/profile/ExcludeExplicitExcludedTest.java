package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.profile.SerializationProfile.Excluded;

public class ExcludeExplicitExcludedTest {

	@Test
	public void testTestTrueIfContainingDollar() throws Exception {
		assertThat(new ExcludeExplicitExcluded().matches(AnObject.class.getDeclaredField("excluded")), is(true));
	}

	@Test
	public void testTestFalseIfNotContainingDollar() throws Exception {
		assertThat(new ExcludeExplicitExcluded().matches(AnObject.class.getDeclaredField("included")), is(false));
	}


	@SuppressWarnings("unused")
	public static class AnObject {
		@Excluded
		private String excluded;
		private String included;

	}
}
