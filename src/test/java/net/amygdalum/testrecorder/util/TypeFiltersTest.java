package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static net.amygdalum.testrecorder.util.TypeFilters.endingWith;
import static net.amygdalum.testrecorder.util.TypeFilters.in;
import static net.amygdalum.testrecorder.util.TypeFilters.startingWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class TypeFiltersTest {

    @Test
    public void testTypeFilters() throws Exception {
        assertThat(TypeFilters.class, isUtilityClass());
    }

    @Test
    public void testStartingWith() throws Exception {
        assertThat(startingWith("S").test(String.class), is(true));
        assertThat(startingWith("L").test(List.class), is(true));
        assertThat(startingWith("L").test(String.class), is(false));
        assertThat(startingWith("S").test(List.class), is(false));
    }

    @Test
    public void testEndingWith() throws Exception {
        assertThat(endingWith("g").test(String.class), is(true));
        assertThat(endingWith("t").test(List.class), is(true));
        assertThat(endingWith("t").test(String.class), is(false));
        assertThat(endingWith("g").test(List.class), is(false));
    }
    
    @Test
    public void testIn() throws Exception {
        assertThat(in("String").test(String.class), is(true));
        assertThat(in("List").test(List.class), is(true));
        assertThat(in("List").test(String.class), is(false));
        assertThat(in("String").test(List.class), is(false));
    }
    
}
