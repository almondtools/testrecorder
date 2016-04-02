package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.SnapshotExcluded;

public class ExcludeExplicitExcludedTest {

	@Test
	public void testTestTrueIfContainingDollar() throws Exception {
		assertThat(new ExcludeExplicitExcluded().test(AnObject.class.getDeclaredField("excluded")), is(true));
	}

	@Test
	public void testTestFalseIfNotContainingDollar() throws Exception {
		assertThat(new ExcludeExplicitExcluded().test(AnObject.class.getDeclaredField("included")), is(false));
	}


	@SuppressWarnings("unused")
	public static class AnObject {
		@SnapshotExcluded
		private String excluded;
		private String included;

	}
}
