package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.MapMatcher.containsEntries;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class MapMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();

        containsEntries(String.class, Integer.class)
            .entry("A", 3)
            .entry("b", 4)
            .describeTo(description);

        assertThat(description.toString()).isEqualTo("containing {<\"A\"=<3>>, <\"b\"=<4>>}");
    }

    @Test
    public void testDescribeToWithMatchers() throws Exception {
        StringDescription description = new StringDescription();

        containsEntries(String.class, Integer.class)
            .entry(equalTo("A"), 3)
            .entry("b", lessThan(4))
            .describeTo(description);

        assertThat(description.toString()).isEqualTo("containing {<\"A\"=<3>>, <\"b\"=a value less than <4>>}");
    }

    @Test
    public void testDescribeToWithUntypedMatchers() throws Exception {
        StringDescription description = new StringDescription();

        containsEntries(Object.class, Object.class)
            .entry((Object) equalTo("A"), 3)
            .entry("b", (Object) lessThan(4))
            .describeTo(description);

        assertThat(description.toString()).isEqualTo("containing {<\"A\"=<3>>, <\"b\"=a value less than <4>>}");
    }

    @Test
    public void testDescribeToWithNull() throws Exception {
        StringDescription description = new StringDescription();

        containsEntries(String.class, Integer.class)
            .entry((String) null, 3)
            .entry("b", (Integer) null)
            .describeTo(description);

        assertThat(description.toString()).isEqualTo("containing {<null=<3>>, <\"b\"=null>}");
    }

    @Test
    public void testMatchesSafelyMatchers() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry(equalTo("A"), 3)
            .entry("b", lessThan(5));

        Map<String, Integer> map = new HashMap<>();
        map.put("A", 3);
        map.put("b", 4);
        assertThat(matcher.matchesSafely(map)).isTrue();
    }

    @Test
    public void testMatchesSafelyWithSuccess() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry("A", 3)
            .entry("b", 4);

        Map<String, Integer> map = new HashMap<>();
        map.put("A", 3);
        map.put("b", 4);
        assertThat(matcher.matchesSafely(map)).isTrue();
    }

    @Test
    public void testMatchesSafelyWithFailureOneElement() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry("A", 3);
        Map<String, Integer> keyMismatch = new HashMap<>();
        keyMismatch.put("B", 3);
        assertThat(matcher.matchesSafely(keyMismatch)).isFalse();

        Map<String, Integer> valueMismatch = new HashMap<>();
        valueMismatch.put("A", 4);
        assertThat(matcher.matchesSafely(valueMismatch)).isFalse();
    }

    @Test
    public void testMatchesSafelyWithFailure() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry("A", 3)
            .entry("b", 4);

        Map<String, Integer> keyMismatch = new HashMap<>();
        keyMismatch.put("A", 3);
        keyMismatch.put("c", 4);
        assertThat(matcher.matchesSafely(keyMismatch)).isFalse();

        Map<String, Integer> valueMismatch = new HashMap<>();
        valueMismatch.put("A", 3);
        valueMismatch.put("c", 4);
        assertThat(matcher.matchesSafely(valueMismatch)).isFalse();

        Map<String, Integer> entryMissing = new HashMap<>();
        entryMissing.put("A", 3);
        assertThat(matcher.matchesSafely(entryMissing)).isFalse();

        Map<String, Integer> entrySurplus = new HashMap<>();
        entrySurplus.put("A", 3);
        entrySurplus.put("b", 4);
        entrySurplus.put("c", 2);
        assertThat(matcher.matchesSafely(entrySurplus)).isFalse();
    }

    @Test
    public void testMatchesNoEntries() throws Exception {
        MapMatcher<String, Integer> matcher = MapMatcher.noEntries(String.class, Integer.class);

        Map<String, Integer> emptyMap = new HashMap<>();
        assertThat(matcher.matchesSafely(emptyMap)).isTrue();

        Map<String, Integer> filledMap = new HashMap<>();
        filledMap.put("key", 66);
        assertThat(matcher.matchesSafely(filledMap)).isFalse();
    }

    @Test
    public void testDescribeMismatchSafelyKeyMismatch() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry(equalTo("A"), 3)
            .entry("b", lessThan(5));

        Map<String, Integer> mismatch = new HashMap<>();
        mismatch.put("A", 3);
        mismatch.put("c", 4);

        StringDescription description = new StringDescription();

        matcher.describeMismatch(mismatch, description);

        assertThat(description.toString()).isEqualTo("missing entries {<\"b\"=a value less than <5>>}, unmatched entries {<was \"c\"=was <4>>}");
    }

    @Test
    public void testDescribeMismatchSafelyValueMismatch() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry(equalTo("A"), 3)
            .entry("b", lessThan(5));

        Map<String, Integer> mismatch = new HashMap<>();
        mismatch.put("A", 3);
        mismatch.put("c", 4);

        StringDescription description = new StringDescription();

        matcher.describeMismatch(mismatch, description);

        assertThat(description.toString()).isEqualTo("missing entries {<\"b\"=a value less than <5>>}, unmatched entries {<was \"c\"=was <4>>}");
    }

    @Test
    public void testDescribeMismatchSafelyEntryMissing() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry(equalTo("A"), 3)
            .entry("b", lessThan(5));

        Map<String, Integer> mismatch = new HashMap<>();
        mismatch.put("A", 3);

        StringDescription description = new StringDescription();

        matcher.describeMismatch(mismatch, description);

        assertThat(description.toString()).isEqualTo("missing entries {<\"b\"=a value less than <5>>}");
    }

    @Test
    public void testDescribeMismatchSafelyEntrySurplus() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry(equalTo("A"), 3)
            .entry("b", lessThan(5));

        Map<String, Integer> mismatch = new HashMap<>();
        mismatch.put("A", 3);
        mismatch.put("b", 4);
        mismatch.put("c", 2);

        StringDescription description = new StringDescription();

        matcher.describeMismatch(mismatch, description);

        assertThat(description.toString()).isEqualTo("unmatched entries {<was \"c\"=was <2>>}");
    }

    @Test
    public void testDescribeMismatchSafelyNull() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry((String) null, (Integer) null);

        Map<String, Integer> mismatch = new HashMap<>();
        mismatch.put("A", 3);

        StringDescription description = new StringDescription();

        matcher.describeMismatch(mismatch, description);

        assertThat(description.toString()).isEqualTo("missing entries {<null=null>}, unmatched entries {<was \"A\"=was <3>>}");
    }
        
    @Test
    public void testDescribeMismatchSafelyNullExpected() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry("A", 3)
            .entry("b", (Integer) null)
            .entry((String) null, 5);

        Map<String, Integer> mismatch = new HashMap<>();
        mismatch.put("A", 3);

        StringDescription description = new StringDescription();

        matcher.describeMismatch(mismatch, description);

        assertThat(description.toString()).isEqualTo("missing entries {<\"b\"=null>, <null=<5>>}");
    }
    
    @Test
    public void testDescribeMismatchSafelyNullFound() throws Exception {
        MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
            .entry("A", 3);

        Map<String, Integer> mismatch = new HashMap<>();
        mismatch.put("A", 3);
        mismatch.put(null, 4);
        mismatch.put("c", null);

        StringDescription description = new StringDescription();

        matcher.describeMismatch(mismatch, description);

        assertThat(description.toString()).isEqualTo("unmatched entries {<was null=was <4>>, <was \"c\"=was null>}");
    }

}
