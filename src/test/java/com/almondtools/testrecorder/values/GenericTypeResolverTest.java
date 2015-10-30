package com.almondtools.testrecorder.values;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class GenericTypeResolverTest {

	@Test
	public void testGenericTypeResolver() throws Exception {
		assertThat(GenericTypeResolver.class, isUtilityClass());
	}

}
