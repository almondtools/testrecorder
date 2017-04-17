package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.GenericMatcher.recursive;
import static net.amygdalum.xrayinterface.IsEquivalent.equivalentTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import net.amygdalum.testrecorder.Wrapped;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;

@SuppressWarnings("unused")
public class GenericMatcherTest {

    @Test
    public void testMatchesSimple() throws Exception {
        assertThat(new GenericMatcher() {
            public String str = "myStr";

        }.mismatchesWith(null, new Simple("myStr")), empty());
    }

    @Test
    public void testMatchingSimple() throws Exception {
        assertThat(new Simple("myStr"), new GenericMatcher() {
            public String str = "myStr";

        }.matching(Simple.class));
    }

    @Test
    public void testNotMatchingSimple() throws Exception {
        assertThat(new Simple("myStr"), not(new GenericMatcher() {
            public String str = "myOtherStr";

        }.matching(Simple.class)));
    }

    @Test
    public void testMatchesComplex() throws Exception {
        assertThat(new GenericMatcher() {
            public Matcher<Simple> simple = new GenericMatcher() {
                public String str = "otherStr";
            }.matching(Simple.class);
        }.mismatchesWith(null, new Complex()), empty());
    }

    @Test
    public void testMatchingNullMatcher() throws Exception {
        assertThat(new GenericMatcher() {
            Matcher<?> str = nullValue();
        }.mismatchesWith(null, new Simple()), empty());
    }

    @Test
    public void testMatchingNullValue() throws Exception {
        assertThat(new GenericMatcher() {
            String str = null;
        }.mismatchesWith(null, new Simple()), empty());
    }

    @Test
    public void testMatchingComplex() throws Exception {
        assertThat(new Complex(), new GenericMatcher() {
            public Matcher<Simple> simple = new GenericMatcher() {
                public String str = "otherStr";
            }.matching(Simple.class);
        }.matching(Complex.class));
    }

    @Test
    public void testNotMatchingComplex() throws Exception {
        assertThat(new Complex(), not(new GenericMatcher() {
            public Matcher<Simple> simple = new GenericMatcher() {
                public String str = "myStr";
            }.matching(Simple.class);
        }.matching(Complex.class)));
    }

    @Test
    public void testMismatchesSimple() throws Exception {
        assertThat(new GenericMatcher() {
            public String str = "myStr";
        }.mismatchesWith(null, new Simple("notMyStr")), contains(equivalentTo(GenericComparisonMatcher.class)
            .withLeft("myStr")
            .withRight("notMyStr")));
    }

    @Test
    public void testRecursive() throws Exception {
        assertThat(recursive(Super.class).matches(new Super()), is(true));
        assertThat(recursive(Super.class).matches(new Sub()), is(true));
        assertThat(recursive(Super.class).matches(new Simple()), is(false));
        assertThat(recursive(Super.class).matches(new Complex()), is(false));
    }

    @Test
    public void testRecursiveWrapped() throws Exception {
        Wrapped wrapped = Wrapped.clazz(Super.class.getName());

        assertThat(recursive(wrapped).matches(new Super()), is(true));
        assertThat(recursive(wrapped).matches(new Sub()), is(true));
        assertThat(recursive(wrapped).matches(new Simple()), is(false));
        assertThat(recursive(wrapped).matches(new Complex()), is(false));
    }

    @Test
    public void testMatchingWrapped() throws Exception {
        Wrapped expected = Wrapped.clazz(Simple.class.getName());
        expected.setField("str", "myStr");

        assertThat(new Simple("myStr"), new GenericMatcher() {
            public String str = "myStr";

        }.matching(expected));
    }

    @Test
    public void testMatchingCasting() throws Exception {
        assertThat((Super) new Sub("myStr"), new GenericMatcher() {
            public String str = "myStr";

        }.matching(Sub.class, Super.class));
    }

    @Test
    public void testMatchingCastingSourceWrapped() throws Exception {
        Wrapped expected = Wrapped.clazz(Sub.class.getName());
        expected.setField("str", "myStr");

        assertThat((Super) new Sub("myStr"), new GenericMatcher() {
            public String str = "myStr";

        }.matching(expected, Super.class));
    }

    @Test
    public void testInternalsMatcherNoMatchesType() throws Exception {
        Matcher<Super> matcher = new GenericMatcher() {
            String str = "str";
        }.matching(Super.class);

        StringDescription desc = new StringDescription();

        assertThat(matcher.matches(new Sub("str")), is(false));
    }

    @Test
    public void testInternalsMatcherDescribeTo() throws Exception {
        Matcher<Simple> matcher = new GenericMatcher() {
            String str = "myStr";
        }.matching(Simple.class);

        StringDescription desc = new StringDescription();
        matcher.describeTo(desc);
        assertThat(desc.toString(), equalTo("net.amygdalum.testrecorder.util.testobjects.Simple {"
            + "\n\tString str: \"myStr\";"
            + "\n}"));
    }

    @Test
    public void testInternalsMatcherDescribeMismatch() throws Exception {
        Matcher<Simple> matcher = new GenericMatcher() {
            String str = "myStr";
        }.matching(Simple.class);

        StringDescription desc = new StringDescription();
        matcher.describeMismatch(new Simple("str"), desc);

        assertThat(desc.toString(), equalTo("net.amygdalum.testrecorder.util.testobjects.Simple {"
            + "\n\tString str: \"str\";"
            + "\n}"
            + "\nfound mismatches at:"
            + "\n\tstr: \"myStr\" != \"str\""));
    }

    @Test
    public void testInternalsMatcherDescribeMismatchEmpty() throws Exception {
        Matcher<Simple> matcher = new GenericMatcher() {
            String str = "myStr";
        }.matching(Simple.class);

        StringDescription desc = new StringDescription();
        matcher.describeMismatch(new Simple("myStr"), desc);

        assertThat(desc.toString(), equalTo(""));
    }

    @Test
    public void testInternalsMatcherDescribeMismatchNull() throws Exception {
        Matcher<Simple> matcher = new GenericMatcher() {
            String str = "myStr";
        }.matching(Simple.class);

        StringDescription desc = new StringDescription();
        matcher.describeMismatch(null, desc);

        assertThat(desc.toString(), equalTo("was null"));
    }

    @Test
    public void testInternalsMatcherDescribeMismatchRecursive() throws Exception {
        Matcher<Complex> matcher = new GenericMatcher() {
            Matcher<?> simple = new GenericMatcher() {
                String str = "str";
            }.matching(Simple.class);
        }.matching(Complex.class);

        StringDescription desc = new StringDescription();
        matcher.describeMismatch(new Complex(), desc);

        assertThat(desc.toString(), equalTo("net.amygdalum.testrecorder.util.testobjects.Complex {"
            + "\n\tSimple simple: <Simple>;"
            + "\n}"
            + "\nfound mismatches at:"
            + "\n\tsimple.str: \"str\" != \"otherStr\""));
    }

    @Test
    public void testCastingMatcherMatches() throws Exception {
        Matcher<Super> matcher = new GenericMatcher() {
            String str = "myStr";
        }.matching(Sub.class, Super.class);

        assertThat(matcher.matches(new Sub("myStr")), is(true));
        assertThat(matcher.matches(new Super("myStr")), is(false));
    }

    @Test
    public void testCastingMatcherNoMatchesType() throws Exception {
        Matcher<Super> matcher = new GenericMatcher() {
            String str = "myStr";
        }.matching(Sub.class, Super.class);

        assertThat(matcher.matches(new Sub("myStr")), is(true));
        assertThat(matcher.matches(new Simple("myStr")), is(false));
    }

    @Test
    public void testCastingMatcherDescribeTo() throws Exception {
        Matcher<Super> matcher = new GenericMatcher() {
            String str = "myStr";
        }.matching(Sub.class, Super.class);

        StringDescription desc = new StringDescription();
        matcher.describeTo(desc);

        assertThat(desc.toString(), equalTo("net.amygdalum.testrecorder.util.testobjects.Sub {"
            + "\n\tString str: \"myStr\";"
            + "\n}"));
    }

    @Test
    public void testCastingMatcherMismatches() throws Exception {
        RecursiveMatcher matcher = (RecursiveMatcher) new GenericMatcher() {
            String str = "myStr";
        }.matching(Sub.class, Super.class);

        assertThat(matcher.mismatchesWith(null, new Sub("myStr")), empty());
    }

    interface GenericComparisonMatcher extends Matcher<GenericComparison> {

        GenericComparisonMatcher withLeft(Object left);

        GenericComparisonMatcher withRight(Object right);
    }
}
