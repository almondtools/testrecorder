package net.amygdalum.testrecorder;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

public class ClassDescriptorTest {

    @Test
    public void testEquals() throws Exception {
        assertThat(ClassDescriptor.of(String.class), satisfiesDefaultEquality()
            .andNotEqualTo(ClassDescriptor.of(Integer.class))
            .andEqualTo(ClassDescriptor.of(String.class)));
    }

    @Test
    public void testGetPackage() throws Exception {
        assertThat(ClassDescriptor.of(String.class).getPackage(), equalTo("java.lang"));
    }

    @Test
    public void testGetSimpleName() throws Exception {
        assertThat(ClassDescriptor.of(String.class).getSimpleName(), equalTo("String"));
    }

}
