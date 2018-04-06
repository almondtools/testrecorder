package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.GenericMatcher.recursive;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.runtime.GenericMatcher;
import net.amygdalum.testrecorder.runtime.GenericObject;
import net.amygdalum.testrecorder.runtime.RecursiveMatcher;
import net.amygdalum.testrecorder.runtime.Wrapped;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.DoubleShadowingObject;
import net.amygdalum.testrecorder.util.testobjects.ShadowingObject;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;

@SuppressWarnings("unused")
public class GenericMatcherTest {

	@Test
	public void testMatchesSimple() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.mismatchesWith(null, new Simple("myStr"))).isEmpty();
	}

	@Test
	public void testMatchingSimple() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.matching(Simple.class).matches(new Simple("myStr"))).isTrue();
	}

	@Test
	public void testNotMatchingSimple() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myOtherStr";

		}.matching(Simple.class).matches(new Simple("myStr"))).isFalse();
	}

	@Test
	public void testMatchesComplex() throws Exception {
		assertThat(new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matching(Simple.class);
		}.mismatchesWith(null, new Complex())).isEmpty();
	}

	@Test
	public void testMatchingNullMatcher() throws Exception {
		assertThat(new GenericMatcher() {
			Matcher<?> str = nullValue();
		}.mismatchesWith(null, new Simple())).isEmpty();
	}

	@Test
	public void testMatchingNullValue() throws Exception {
		assertThat(new GenericMatcher() {
			String str = null;
		}.mismatchesWith(null, new Simple())).isEmpty();
	}

	@Test
	public void testMatchingComplex() throws Exception {
		assertThat(new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "otherStr";
			}.matching(Simple.class);
		}.matching(Complex.class).matches(new Complex())).isTrue();
	}

	@Test
	public void testNotMatchingComplex() throws Exception {
		assertThat(new GenericMatcher() {
			public Matcher<Simple> simple = new GenericMatcher() {
				public String str = "myStr";
			}.matching(Simple.class);
		}.matching(Complex.class).matches(new Complex())).isFalse();
	}

	@Test
	public void testMismatchesSimple() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";
		}.mismatchesWith(null, new Simple("notMyStr"))).anySatisfy(mismatch -> {
			assertThat(mismatch.getLeft()).isEqualTo("myStr");
			assertThat(mismatch.getRight()).isEqualTo("notMyStr");
		});
	}

	@Test
	public void testRecursive() throws Exception {
		assertThat(recursive(Super.class).matches(new Super())).isTrue();
		assertThat(recursive(Super.class).matches(new Sub())).isTrue();
		assertThat(recursive(Super.class).matches(new Simple())).isFalse();
		assertThat(recursive(Super.class).matches(new Complex())).isFalse();
	}

	@Test
	public void testRecursiveWrapped() throws Exception {
		Wrapped wrapped = Wrapped.clazz(Super.class.getName());

		assertThat(recursive(wrapped).matches(new Super())).isTrue();
		assertThat(recursive(wrapped).matches(new Sub())).isTrue();
		assertThat(recursive(wrapped).matches(new Simple())).isFalse();
		assertThat(recursive(wrapped).matches(new Complex())).isFalse();
	}

	@Test
	public void testMatchingWrapped() throws Exception {
		Wrapped expected = Wrapped.clazz(Simple.class.getName());
		expected.setField("str", "myStr");

		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.matching(expected).matches(new Simple("myStr"))).isTrue();
	}

	@Test
	public void testMatchingCasting() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.matching(Sub.class, Super.class).matches((Super) new Sub("myStr"))).isTrue();
	}

	@Test
	public void testMatchingCastingSourceWrapped() throws Exception {
		Wrapped expected = Wrapped.clazz(Sub.class.getName());
		expected.setField("str", "myStr");

		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.matching(expected, Super.class).matches((Super) new Sub("myStr"))).isTrue();
	}

	@Test
	public void testInternalsMatcherNoMatchesType() throws Exception {
		Matcher<Super> matcher = new GenericMatcher() {
			String str = "str";
		}.matching(Super.class);

		assertThat(matcher.matches(new Sub("str"))).isFalse();
	}

	@Test
	public void testInternalsMatcherDescribeTo() throws Exception {
		Matcher<Simple> matcher = new GenericMatcher() {
			String str = "myStr";
		}.matching(Simple.class);

		StringDescription desc = new StringDescription();
		matcher.describeTo(desc);
		assertThat(desc.toString()).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Simple {"
			+ "\n\tString str: \"myStr\";"
			+ "\n}");
	}

	@Test
	public void testInternalsMatcherDescribeMismatch() throws Exception {
		Matcher<Simple> matcher = new GenericMatcher() {
			String str = "myStr";
		}.matching(Simple.class);

		StringDescription desc = new StringDescription();
		matcher.describeMismatch(new Simple("str"), desc);

		assertThat(desc.toString()).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Simple {"
			+ "\n\tString str: \"str\";"
			+ "\n}"
			+ "\nfound mismatches at:"
			+ "\n\tstr: \"myStr\" != \"str\"");
	}

	@Test
	public void testInternalsMatcherDescribeMismatchEmpty() throws Exception {
		Matcher<Simple> matcher = new GenericMatcher() {
			String str = "myStr";
		}.matching(Simple.class);

		StringDescription desc = new StringDescription();
		matcher.describeMismatch(new Simple("myStr"), desc);

		assertThat(desc.toString()).isEqualTo("");
	}

	@Test
	public void testInternalsMatcherDescribeMismatchNull() throws Exception {
		Matcher<Simple> matcher = new GenericMatcher() {
			String str = "myStr";
		}.matching(Simple.class);

		StringDescription desc = new StringDescription();
		matcher.describeMismatch(null, desc);

		assertThat(desc.toString()).isEqualTo("was null");
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

		assertThat(desc.toString()).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Complex {"
			+ "\n\tSimple simple: <Simple>;"
			+ "\n}"
			+ "\nfound mismatches at:"
			+ "\n\tsimple.str: \"str\" != \"otherStr\"");
	}

	@Test
	public void testCastingMatcherMatches() throws Exception {
		Matcher<Super> matcher = new GenericMatcher() {
			String str = "myStr";
		}.matching(Sub.class, Super.class);

		assertThat(matcher.matches(new Sub("myStr"))).isTrue();
		assertThat(matcher.matches(new Super("myStr"))).isFalse();
	}

	@Test
	public void testCastingMatcherNoMatchesType() throws Exception {
		Matcher<Super> matcher = new GenericMatcher() {
			String str = "myStr";
		}.matching(Sub.class, Super.class);

		assertThat(matcher.matches(new Sub("myStr"))).isTrue();
		assertThat(matcher.matches(new Simple("myStr"))).isFalse();
	}

	@Test
	public void testCastingMatcherDescribeTo() throws Exception {
		Matcher<Super> matcher = new GenericMatcher() {
			String str = "myStr";
		}.matching(Sub.class, Super.class);

		StringDescription desc = new StringDescription();
		matcher.describeTo(desc);

		assertThat(desc.toString()).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Sub {"
			+ "\n\tString str: \"myStr\";"
			+ "\n}");
	}

	@Test
	public void testCastingMatcherMismatches() throws Exception {
		RecursiveMatcher matcher = (RecursiveMatcher) new GenericMatcher() {
			String str = "myStr";
		}.matching(Sub.class, Super.class);

		assertThat(matcher.mismatchesWith(null, new Sub("myStr"))).isEmpty();
	}

	@Test
	public void testShadowingObject() throws Exception {
		Matcher<ShadowingObject> matcher = new GenericMatcher() {
			int ShadowedObject$field = 42;
			String ShadowingObject$field = "field";
		}.matching(ShadowingObject.class);
		assertThat(matcher.matches(new ShadowingObject("field", 42))).isTrue();
	}

	@Test
	public void testDoubleShadowingObject() throws Exception {
		Matcher<DoubleShadowingObject> matcher = new GenericMatcher() {
			int ShadowedObject$field = 42;
			String ShadowingObject$field = "field";
			String DoubleShadowingObject$field = "fieldshadowing";
		}.matching(DoubleShadowingObject.class);
		assertThat(matcher.matches(new DoubleShadowingObject("fieldshadowing", "field", 42))).isTrue();
	}

	@Test
	public void testMismatchesNull() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.matching(Simple.class).matches((Simple) null)).isFalse();

		assertThat(new GenericMatcher() {
			public String str = "myStr";

		}.matching(Simple.class).matches(new Simple(null))).isFalse();

		assertThat(new GenericMatcher() {
			public Matcher<?> simple = new GenericMatcher() {
				public String str = "myStr";
			}.matching(Simple.class);
		}.matching(Complex.class).matches(new GenericObject() {
			public Simple simple = null;
		}.as(Complex.class))).isFalse();
	}

	@Test
	public void testMatchesNull() throws Exception {
		assertThat(new GenericMatcher() {
			public String str = null;

		}.matching(Simple.class).matches((Simple) new Simple(null))).isTrue();
	}

	@Test
	public void testMatchesSyntheticClasses() throws Exception {
		Functional f = x -> x * x;
		assertThat(new GenericMatcher() {
		}.matching(String.class, Object.class).matches(f)).isFalse();
		assertThat(new GenericMatcher() {
		}.matching(Functional.class).matches(f)).isTrue();

	}

	@Test
	public void testDescribeWithValues() throws Exception {
		StringDescription description = new StringDescription();
		Matcher<Simple> matching = new GenericMatcher() {
			String str = "str";

		}.matching(Simple.class);
		matching.describeTo(description);

		assertThat(description.toString()).containsWildcardPattern("net.amygdalum.testrecorder.util.testobjects.Simple {*String str: \"str\";*}");
	}

	@Test
	public void testDescribeWithMatchers() throws Exception {
		StringDescription description = new StringDescription();
		Matcher<Simple> matching = new GenericMatcher() {
			Matcher<?> str = containsString("st");

		}.matching(Simple.class);
		matching.describeTo(description);

		assertThat(description.toString()).containsWildcardPattern("net.amygdalum.testrecorder.util.testobjects.Simple {*String str: a string containing \"st\";*}");
	}

	interface Functional {
		int func(int x);
	}
}
