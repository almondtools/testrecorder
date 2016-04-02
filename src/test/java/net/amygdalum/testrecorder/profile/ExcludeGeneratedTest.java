package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ExcludeGeneratedTest {

	@Test
	public void testTestTrueIfContainingDollar() throws Exception {
		assertThat(new ExcludeGenerated().test(AnObject.class.getDeclaredField("$generated")), is(true));
	}

	@Test
	public void testTestFalseIfNotContainingDollar() throws Exception {
		assertThat(new ExcludeGenerated().test(AnObject.class.getDeclaredField("notgenerated")), is(false));
	}

	@SuppressWarnings("unused")
	public static class AnObject {
		private String $generated;
		private String notgenerated;

	}
}
