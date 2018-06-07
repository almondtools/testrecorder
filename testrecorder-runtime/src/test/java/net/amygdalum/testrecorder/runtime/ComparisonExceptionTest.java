package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ComparisonExceptionTest {

	@Test
	void testFailed() throws Exception {
		assertThat(new ComparisonException().failed()).isTrue();
		assertThat(new ComparisonException(false).failed()).isFalse();
	}

}
