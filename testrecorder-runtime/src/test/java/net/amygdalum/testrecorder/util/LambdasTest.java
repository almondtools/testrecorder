package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LambdasTest {

	@Test
	public void testDebug() throws Exception {
		assertThat(Lambdas.class).satisfies(utilityClass().conventions());
	}
	
}
