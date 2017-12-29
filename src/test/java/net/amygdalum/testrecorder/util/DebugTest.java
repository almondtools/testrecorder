package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

public class DebugTest {

	@Test
	public void testDebug() throws Exception {
		assertThat(Debug.class, isUtilityClass());
	}
	
	
}
