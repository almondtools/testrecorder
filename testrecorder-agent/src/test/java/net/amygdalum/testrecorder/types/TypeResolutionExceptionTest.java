package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TypeResolutionExceptionTest {

	@Test
	public void testTypeResolutionException() throws Exception {
		assertThat(new TypeResolutionException("msg"))
			.isInstanceOf(TypeResolutionException.class)
			.hasMessage("msg");
	}

}
