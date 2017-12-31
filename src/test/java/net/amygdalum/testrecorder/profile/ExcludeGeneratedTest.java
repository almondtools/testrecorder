package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ExcludeGeneratedTest {

	@Test
	public void testTestTrueIfContainingDollar() throws Exception {
		assertThat(new ExcludeGenerated().matches(AnObject.class.getDeclaredField("$generated"))).isTrue();
	}

	@Test
	public void testTestFalseIfNotContainingDollar() throws Exception {
		assertThat(new ExcludeGenerated().matches(AnObject.class.getDeclaredField("notgenerated"))).isFalse();
	}

	@SuppressWarnings("unused")
	public static class AnObject {
		private String $generated;
		private String notgenerated;

	}
}
