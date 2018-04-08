package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static net.amygdalum.testrecorder.util.TypeFilters.endingWith;
import static net.amygdalum.testrecorder.util.TypeFilters.in;
import static net.amygdalum.testrecorder.util.TypeFilters.startingWith;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TypeFiltersTest {

    @Test
    public void testTypeFilters() throws Exception {
        assertThat(TypeFilters.class).satisfies(utilityClass().conventions());
    }

    @Test
    public void testStartingWith() throws Exception {
        assertThat(startingWith("S").test(String.class)).isTrue();
        assertThat(startingWith("L").test(List.class)).isTrue();
        assertThat(startingWith("L").test(String.class)).isFalse();
        assertThat(startingWith("S").test(List.class)).isFalse();
    }

    @Test
    public void testEndingWith() throws Exception {
        assertThat(endingWith("g").test(String.class)).isTrue();
        assertThat(endingWith("t").test(List.class)).isTrue();
        assertThat(endingWith("t").test(String.class)).isFalse();
        assertThat(endingWith("g").test(List.class)).isFalse();
    }
    
    @Test
    public void testIn() throws Exception {
        assertThat(in("String").test(String.class)).isTrue();
        assertThat(in("List").test(List.class)).isTrue();
        assertThat(in("List").test(String.class)).isFalse();
        assertThat(in("String").test(List.class)).isFalse();
    }
    
}
