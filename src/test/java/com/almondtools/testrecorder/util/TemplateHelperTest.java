package com.almondtools.testrecorder.util;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.testrecorder.util.TemplateHelper;

public class TemplateHelperTest {

	@Test
	public void testTemplateHelper() throws Exception {
		assertThat(TemplateHelper.class, isUtilityClass());
	}

}
