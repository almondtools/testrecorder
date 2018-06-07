package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TemplatesTest {

	@Test
	public void testTemplates() throws Exception {
		assertThat(Templates.class).satisfies(utilityClass().conventions());
	}

}
