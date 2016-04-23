package net.amygdalum.testrecorder.deserializers;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TemplatesTest {

	@Test
	public void testTemplates() throws Exception {
		assertThat(Templates.class, isUtilityClass());
	}

}
