package net.amygdalum.testrecorder.util;

import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.conmatch.conventions.EnumMatcher;

public class GenericComparatorResultTest {

    @Test
    public void testGenericComparatorResult() throws Exception {
        assertThat(GenericComparatorResult.class, EnumMatcher.isEnum().withElements(3));
    }
}
