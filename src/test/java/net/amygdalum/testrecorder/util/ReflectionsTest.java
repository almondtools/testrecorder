package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ReflectionsTest {

    @Test
    public void testReflections() throws Exception {
        assertThat(Reflections.class).satisfies(utilityClass().conventions());
    }

}
