package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.util.Pair;

public class PairTest {

    @Test
    public void testPair() throws Exception {
        assertThat(new Pair<String, Integer>("string", 11).getElement1(), equalTo("string"));
        assertThat(new Pair<String, Integer>("string", 11).getElement2(), equalTo(11));
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        assertThat(new Pair<String, Integer>("string", 11), satisfiesDefaultEquality()
            .andEqualTo(new Pair<String, Integer>("string", 11))
            .andNotEqualTo(new Pair<String, Integer>("s",11))
            .andNotEqualTo(new Pair<String, Integer>("string",12))
            .andNotEqualTo(new Pair<String, String>("string","string"))
            .andNotEqualTo(new Pair<Integer, Integer>(11,11)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testZip() throws Exception {
        Pair<String, Integer>[] zipped = Pair.zip(new String[]{"s1","s2"}, new Integer[]{1,2});
        assertThat(zipped, arrayContaining(new Pair<>("s1",1), new Pair<>("s2",2)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testZipWithUnmatchedArraLength() throws Exception {
        Pair.zip(new String[]{"s1","s2"}, new Integer[]{1});
    }

}
