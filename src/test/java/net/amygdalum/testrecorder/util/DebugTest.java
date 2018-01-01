package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DebugTest {

	@Test
	public void testDebug() throws Exception {
		assertThat(Debug.class).satisfies(utilityClass().conventions());
	}
	
}
