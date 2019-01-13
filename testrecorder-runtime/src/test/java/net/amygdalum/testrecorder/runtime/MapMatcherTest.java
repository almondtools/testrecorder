package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.MapMatcher.containsEntries;
import static net.amygdalum.testrecorder.runtime.MapMatcher.noEntries;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MapMatcherTest {

	@Nested
	class testDescribeTo {
		@Test
		void onCommon() throws Exception {
			StringDescription description = new StringDescription();

			containsEntries(String.class, Integer.class)
				.entry("A", 3)
				.entry("b", 4)
				.describeTo(description);

			assertThat(description.toString()).isEqualTo("containing {<\"A\"=<3>>, <\"b\"=<4>>}");
		}

		@Test
		void withMatchers() throws Exception {
			StringDescription description = new StringDescription();

			containsEntries(String.class, Integer.class)
				.entry(equalTo("A"), 3)
				.entry("b", lessThan(4))
				.describeTo(description);

			assertThat(description.toString()).isEqualTo("containing {<\"A\"=<3>>, <\"b\"=a value less than <4>>}");
		}

		@Test
		void withUntypedMatchers() throws Exception {
			StringDescription description = new StringDescription();

			containsEntries(Object.class, Object.class)
				.entry((Object) equalTo("A"), 3)
				.entry("b", (Object) lessThan(4))
				.describeTo(description);

			assertThat(description.toString()).isEqualTo("containing {<\"A\"=<3>>, <\"b\"=a value less than <4>>}");
		}

		@Test
		void withNull() throws Exception {
			StringDescription description = new StringDescription();

			containsEntries(String.class, Integer.class)
				.entry((String) null, 3)
				.entry("b", (Integer) null)
				.describeTo(description);

			assertThat(description.toString()).isEqualTo("containing {<null=<3>>, <\"b\"=null>}");
		}
	}

	@Nested
	class testMatchesSafely {
		@Nested
		class usingTypedFactory {

			@Test
			void withSuccess() throws Exception {
				MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
					.entry("A", 3)
					.entry("b", 4);

				Map<String, Integer> map = new HashMap<>();
				map.put("A", 3);
				map.put("b", 4);
				assertThat(matcher.matchesSafely(map)).isTrue();
			}

			@Test
			void withFailure() throws Exception {
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
			void withFailureOneElement() throws Exception {
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
			void withNoEntries() throws Exception {
				MapMatcher<String, Integer> matcher = noEntries(String.class, Integer.class);

				Map<String, Integer> emptyMap = new HashMap<>();
				assertThat(matcher.matchesSafely(emptyMap)).isTrue();

				Map<String, Integer> filledMap = new HashMap<>();
				filledMap.put("key", 66);
				assertThat(matcher.matchesSafely(filledMap)).isFalse();
			}

			@Test
			void withMatchers() throws Exception {
				MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
					.entry(equalTo("A"), 3)
					.entry("b", lessThan(5));

				Map<String, Integer> map = new HashMap<>();
				map.put("A", 3);
				map.put("b", 4);
				assertThat(matcher.matchesSafely(map)).isTrue();
			}
		}

		@Nested
		@SuppressWarnings("unchecked")
		class usingErasedFactory {
			@Test
			void withSuccess() throws Exception {
				MapMatcher<String, Integer> matcher = containsEntries()
					.entry("A", 3)
					.entry("b", 4);

				Map<String, Integer> map = new HashMap<>();
				map.put("A", 3);
				map.put("b", 4);
				assertThat(matcher.matchesSafely(map)).isTrue();
			}

			@Test
			void withFailure() throws Exception {
				MapMatcher<String, Integer> matcher = containsEntries()
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
			void withFailureOneElement() throws Exception {
				MapMatcher<String, Integer> matcher = containsEntries()
					.entry("A", 3);
				Map<String, Integer> keyMismatch = new HashMap<>();
				keyMismatch.put("B", 3);
				assertThat(matcher.matchesSafely(keyMismatch)).isFalse();

				Map<String, Integer> valueMismatch = new HashMap<>();
				valueMismatch.put("A", 4);
				assertThat(matcher.matchesSafely(valueMismatch)).isFalse();
			}

			@Test
			void withNoEntries() throws Exception {
				MapMatcher<String, Integer> matcher = noEntries();

				Map<String, Integer> emptyMap = new HashMap<>();
				assertThat(matcher.matchesSafely(emptyMap)).isTrue();

				Map<String, Integer> filledMap = new HashMap<>();
				filledMap.put("key", 66);
				assertThat(matcher.matchesSafely(filledMap)).isFalse();
			}

			@Test
			void withMatchers() throws Exception {
				MapMatcher<String, Integer> matcher = containsEntries()
					.entry(equalTo("A"), 3)
					.entry("b", lessThan(5));

				Map<String, Integer> map = new HashMap<>();
				map.put("A", 3);
				map.put("b", 4);
				assertThat(matcher.matchesSafely(map)).isTrue();
			}
		}
	}

	@Nested
	class testDescribeMismatchSafely {
		@Test
		void withKeyMismatch() throws Exception {
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
		void withValueMismatch() throws Exception {
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
		void withEntryMissing() throws Exception {
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
		void withEntrySurplus() throws Exception {
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
		void withNull() throws Exception {
			MapMatcher<String, Integer> matcher = containsEntries(String.class, Integer.class)
				.entry((String) null, (Integer) null);

			Map<String, Integer> mismatch = new HashMap<>();
			mismatch.put("A", 3);

			StringDescription description = new StringDescription();

			matcher.describeMismatch(mismatch, description);

			assertThat(description.toString()).isEqualTo("missing entries {<null=null>}, unmatched entries {<was \"A\"=was <3>>}");
		}

		@Test
		void withNullExpected() throws Exception {
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
		void withNullFound() throws Exception {
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

}
