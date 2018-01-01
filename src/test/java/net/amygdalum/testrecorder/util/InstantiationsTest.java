package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class InstantiationsTest {

	@Test
	public void testInstantiations() throws Exception {
		assertThat(Instantiations.class).satisfies(utilityClass().conventions());
	}
}
