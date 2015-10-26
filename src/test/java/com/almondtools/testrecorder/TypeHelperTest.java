package com.almondtools.testrecorder;

import static com.almondtools.util.objects.UtilityClassMatcher.isUtilityClass;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TypeHelperTest {

	@Test
	public void testTemplateHelper() throws Exception {
		assertThat(TypeHelper.class, isUtilityClass());
	}

}
