package com.almondtools.testrecorder;

import static com.almondtools.util.objects.UtilityClassMatcher.isUtilityClass;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.testrecorder.TemplateHelper;

public class TemplateHelperTest {

	@Test
	public void testTemplateHelper() throws Exception {
		assertThat(TemplateHelper.class, isUtilityClass());
	}

}
