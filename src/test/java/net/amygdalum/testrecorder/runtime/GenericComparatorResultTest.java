package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.assertjconventions.conventions.DefaultEnum;

public class GenericComparatorResultTest {

	@Test
	public void testGenericComparatorResult() throws Exception {
		assertThat(GenericComparatorResult.class).satisfies(DefaultEnum.defaultEnum()
			.withElements(3)
			.conventions());
	}
}
