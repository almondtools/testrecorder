package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.runtime.ContainsInOrderMatcher.containsInOrder;
import static net.amygdalum.testrecorder.runtime.ContainsMatcher.empty;
import static net.amygdalum.testrecorder.runtime.GenericMatcher.recursive;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.DoubleShadowingObject;
import net.amygdalum.testrecorder.util.testobjects.ShadowingObject;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.Sub;
import net.amygdalum.testrecorder.util.testobjects.Super;
import net.amygdalum.testrecorder.util.testobjects.TreeNode;

@SuppressWarnings("unused")
public class GenericMatcherTest {

	@Nested
	class InternalsMatcher {
		@Nested
		class testMismatchesWith {

			@Test
			void onSimple() throws Exception {
				assertThat(new GenericMatcher() {
					public String str = "myStr";

				}.mismatchesWith(null, new Simple("myStr"))).isEmpty();
			}

			@Test
			void onComplex() throws Exception {
				assertThat(new GenericMatcher() {
					public Matcher<Simple> simple = new GenericMatcher() {
						public String str = "otherStr";
					}.matching(Simple.class);
				}.mismatchesWith(null, new Complex())).isEmpty();
			}

			@Test
			void onSimpleMatchingNull() throws Exception {
				assertThat(new GenericMatcher() {
					String str = null;
				}.mismatchesWith(null, new Simple())).isEmpty();
			}

			@Test
			void onSimpleMatcherMatchingNull() throws Exception {
				assertThat(new GenericMatcher() {
					Matcher<?> str = nullValue();
				}.mismatchesWith(null, new Simple())).isEmpty();
			}

			@Test
			void onSimpleFailing() throws Exception {
				assertThat(new GenericMatcher() {
					public String str = "myStr";
				}.mismatchesWith(null, new Simple("notMyStr"))).anySatisfy(mismatch -> {
					assertThat(mismatch.getLeft()).isEqualTo("myStr");
					assertThat(mismatch.getRight()).isEqualTo("notMyStr");
				});
			}

		}

		@Nested
		class testMatches {
			@Test
			void onSimple() throws Exception {
				assertThat(new GenericMatcher() {
					public String str = "myStr";

				}.matching(Simple.class).matches(new Simple("myStr"))).isTrue();
			}

			@Test
			void onSimpleWithFailure() throws Exception {
				assertThat(new GenericMatcher() {
					public String str = "myOtherStr";

				}.matching(Simple.class).matches(new Simple("myStr"))).isFalse();
			}

			@Test
			void onComplex() throws Exception {
				assertThat(new GenericMatcher() {
					public Matcher<Simple> simple = new GenericMatcher() {
						public String str = "otherStr";
					}.matching(Simple.class);
				}.matching(Complex.class).matches(new Complex())).isTrue();
			}

			@Test
			void onComplexWithFailure() throws Exception {
				assertThat(new GenericMatcher() {
					public Matcher<Simple> simple = new GenericMatcher() {
						public String str = "myStr";
					}.matching(Simple.class);
				}.matching(Complex.class).matches(new Complex())).isFalse();
			}

			@Test
			void onWrapped() throws Exception {
				Wrapped expected = Wrapped.clazz(Simple.class.getName());
				expected.setField("str", "myStr");

				assertThat(new GenericMatcher() {
					public String str = "myStr";

				}.matching(expected).matches(new Simple("myStr"))).isTrue();
			}

			@Test
			void onWrongType() throws Exception {
				Matcher<Super> matcher = new GenericMatcher() {
					String str = "str";
				}.matching(Super.class);

				assertThat(matcher.matches(new Sub("str"))).isFalse();
			}

			@Test
			void onNull() throws Exception {
				assertThat(new GenericMatcher() {
					public String str = null;

				}.matching(Simple.class).matches((Simple) new Simple(null))).isTrue();
			}

			@Test
			void onNullWithFailure() throws Exception {
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
			void onSyntheticClasses() throws Exception {
				Functional f = x -> x * x;

				assertThat(new GenericMatcher() {
				}.matching(Functional.class).matches(f)).isTrue();
			}

		}

		@Nested
		class testDescribeTo {
			@Test
			void onSimple() throws Exception {
				Matcher<Simple> matcher = new GenericMatcher() {
					String str = "str";
				}.matching(Simple.class);

				StringDescription description = new StringDescription();
				matcher.describeTo(description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "net.amygdalum.testrecorder.util.testobjects.Simple {*"
					+ "String str: \"str\";*"
					+ "}");
			}

			@Test
			void onSimpleMatcher() throws Exception {
				Matcher<Simple> matcher = new GenericMatcher() {
					Matcher<?> str = containsString("st");

				}.matching(Simple.class);

				StringDescription description = new StringDescription();
				matcher.describeTo(description);

				assertThat(description.toString()).containsWildcardPattern(""
					+ "net.amygdalum.testrecorder.util.testobjects.Simple {*"
					+ "String str: a string containing \"st\";*"
					+ "}");
			}

		}

		@Nested
		class testDescribeMismatch {
			@Test
			void onSimple() throws Exception {
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
			void onEmpty() throws Exception {
				Matcher<Simple> matcher = new GenericMatcher() {
					String str = "myStr";
				}.matching(Simple.class);

				StringDescription desc = new StringDescription();
				matcher.describeMismatch(new Simple("myStr"), desc);

				assertThat(desc.toString()).isEqualTo("");
			}

			@Test
			void onNull() throws Exception {
				Matcher<Simple> matcher = new GenericMatcher() {
					String str = "myStr";
				}.matching(Simple.class);

				StringDescription desc = new StringDescription();
				matcher.describeMismatch(null, desc);

				assertThat(desc.toString()).isEqualTo("was null");
			}

			@Test
			void onComplex() throws Exception {
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

		}
	}

	@Nested
	class MatcherFromRecursive {
		@Nested
		class testMatches {
			@Test
			void onCommon() throws Exception {
				assertThat(recursive(Super.class).matches(new Super())).isTrue();
				assertThat(recursive(Super.class).matches(new Sub())).isTrue();
				assertThat(recursive(Super.class).matches(new Simple())).isFalse();
				assertThat(recursive(Super.class).matches(new Complex())).isFalse();
			}

			@Test
			void onWrapped() throws Exception {
				Wrapped wrapped = Wrapped.clazz(Super.class.getName());

				assertThat(recursive(wrapped).matches(new Super())).isTrue();
				assertThat(recursive(wrapped).matches(new Sub())).isTrue();
				assertThat(recursive(wrapped).matches(new Simple())).isFalse();
				assertThat(recursive(wrapped).matches(new Complex())).isFalse();
			}
		}
	}

	@Nested
	class CastingMatcher {
		@Nested
		class testMatches {
			@Test
			void onSimple() throws Exception {
				Matcher<Super> matcher = new GenericMatcher() {
					String str = "myStr";
				}.matching(Sub.class, Super.class);

				assertThat(matcher.matches(new Sub("myStr"))).isTrue();
			}

			@Test
			void onSimpleWithFailure() throws Exception {
				Matcher<Super> matcher = new GenericMatcher() {
					String str = "myStr";
				}.matching(Sub.class, Super.class);

				assertThat(matcher.matches(new Super("myStr"))).isFalse();
			}

			@Test
			void onWrapped() throws Exception {
				Wrapped expected = Wrapped.clazz(Sub.class.getName());
				expected.setField("str", "myStr");

				assertThat(new GenericMatcher() {
					public String str = "myStr";

				}.matching(expected, Super.class).matches((Super) new Sub("myStr"))).isTrue();
			}

			@Test
			void onWrongType() throws Exception {
				Matcher<Super> matcher = new GenericMatcher() {
					String str = "myStr";
				}.matching(Sub.class, Super.class);

				assertThat(matcher.matches(new Simple("myStr"))).isFalse();
			}

			@Test
			void onSyntheticClasses() throws Exception {
				Functional f = x -> x * x;

				assertThat(new GenericMatcher() {
				}.matching(Functional.class).matches(f)).isTrue();

			}
		}

		@Nested
		class testMismatchesWith {
			@Test
			void onSimple() throws Exception {
				RecursiveMatcher matcher = (RecursiveMatcher) new GenericMatcher() {
					String str = "myStr";
				}.matching(Sub.class, Super.class);

				assertThat(matcher.mismatchesWith(null, new Sub("myStr"))).isEmpty();
			}
		}

		@Test
		void testDescribeTo() throws Exception {
			Matcher<Super> matcher = new GenericMatcher() {
				String str = "myStr";
			}.matching(Sub.class, Super.class);

			StringDescription desc = new StringDescription();
			matcher.describeTo(desc);

			assertThat(desc.toString()).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Sub {"
				+ "\n\tString str: \"myStr\";"
				+ "\n}");
		}

	}

	@Nested
	class Scenarios {
		@Test
		void shadowingObjects() throws Exception {
			Matcher<ShadowingObject> matcher = new GenericMatcher() {
				int ShadowedObject$field = 42;
				String ShadowingObject$field = "field";
			}.matching(ShadowingObject.class);
			assertThat(matcher.matches(new ShadowingObject("field", 42))).isTrue();
		}

		@Test
		void doubleShadowingObjects() throws Exception {
			Matcher<DoubleShadowingObject> matcher = new GenericMatcher() {
				int ShadowedObject$field = 42;
				String ShadowingObject$field = "field";
				String DoubleShadowingObject$field = "fieldshadowing";
			}.matching(DoubleShadowingObject.class);
			assertThat(matcher.matches(new DoubleShadowingObject("fieldshadowing", "field", 42))).isTrue();
		}

		@Test
		void mismatchOnComplexMatch() throws Exception {
			Matcher<TreeNode> matcher = new GenericMatcher() {
				Matcher<?> children = containsInOrder(TreeNode.class)
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf1";
					}.matching(TreeNode.class))
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf2";
					}.matching(TreeNode.class));
				Object payload = "root";
			}.matching(TreeNode.class);

			TreeNode matching = new TreeNode().setPayload("root")
				.addChild(new TreeNode().setPayload("leaf1"))
				.addChild(new TreeNode().setPayload("leaf2"));

			StringDescription desc = new StringDescription();
			matcher.describeMismatch(matching, desc);

			assertThat(desc.toString()).isEqualTo("");
		}

		@Test
		void mismatchMissing() throws Exception {
			Matcher<TreeNode> matcher = new GenericMatcher() {
				Matcher<?> children = containsInOrder(TreeNode.class)
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf1";
					}.matching(TreeNode.class))
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf2";
					}.matching(TreeNode.class));
				Object payload = "root";
			}.matching(TreeNode.class);

			TreeNode matching = new TreeNode().setPayload("root")
				.addChild(new TreeNode().setPayload("leaf1"));

			StringDescription desc = new StringDescription();
			matcher.describeMismatch(matching, desc);

			assertThat(desc.toString()).containsWildcardPattern("missing 1 elements*leaf2");
		}

		@Test
		void mismatchSurplus() throws Exception {
			Matcher<TreeNode> matcher = new GenericMatcher() {
				Matcher<?> children = containsInOrder(TreeNode.class)
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf1";
					}.matching(TreeNode.class))
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf2";
					}.matching(TreeNode.class));
				Object payload = "root";
			}.matching(TreeNode.class);

			TreeNode matching = new TreeNode().setPayload("root")
				.addChild(new TreeNode().setPayload("leaf1"))
				.addChild(new TreeNode().setPayload("leaf2"))
				.addChild(new TreeNode().setPayload("leaf3"));

			StringDescription desc = new StringDescription();
			matcher.describeMismatch(matching, desc);

			assertThat(desc.toString()).containsWildcardPattern("found 1 elements surplus*leaf3");
		}

		@Test
		void mismatchUnexpected() throws Exception {
			Matcher<TreeNode> matcher = new GenericMatcher() {
				Matcher<?> children = containsInOrder(TreeNode.class)
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf1";
					}.matching(TreeNode.class))
					.element(new GenericMatcher() {
						Matcher<?> children = empty();
						Object payload = "leaf2";
					}.matching(TreeNode.class));
				Object payload = "root";
			}.matching(TreeNode.class);

			TreeNode matching = new TreeNode().setPayload("root")
				.addChild(new TreeNode().setPayload("leaf1"))
				.addChild(new TreeNode().setPayload("leaf3"));

			StringDescription desc = new StringDescription();
			matcher.describeMismatch(matching, desc);

			assertThat(desc.toString()).contains("\"leaf2\" != \"leaf3\"");
		}
	}

	interface Functional {
		int func(int x);
	}
}
