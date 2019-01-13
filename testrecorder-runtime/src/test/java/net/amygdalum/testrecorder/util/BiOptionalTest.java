package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class BiOptionalTest {

	@Nested
	class testMap {
		@Test
		void withFunctionalDefaults() throws Exception {
			assertThat(BiOptional.of(47, 11)
				.map((x, y) -> x + ":" + y, (Integer x) -> x + ":0", (Integer y) -> (String) "0:" + y)
				.orElse("0:0"))
					.isEqualTo("47:11");
			assertThat(BiOptional.ofNullable(47, 11)
				.map((x, y) -> x + ":" + y, (Integer x) -> x + ":0", (Integer y) -> (String) "0:" + y)
				.orElse("0:0"))
					.isEqualTo("47:11");
			assertThat(BiOptional.ofNullable(47, null)
				.map((x, y) -> x + ":" + y, (Integer x) -> x + ":0", (Integer y) -> (String) "0:" + y)
				.orElse("0:0"))
					.isEqualTo("47:0");
			assertThat(BiOptional.ofNullable(null, 11)
				.map((x, y) -> x + ":" + y, (Integer x) -> x + ":0", (Integer y) -> (String) "0:" + y)
				.orElse("0:0"))
					.isEqualTo("0:11");
			assertThat(BiOptional.ofNullable((Integer) null, (Integer) null)
				.map((x, y) -> x + ":" + y, (Integer x) -> x + ":0", (Integer y) -> (String) "0:" + y)
				.orElse("0:0"))
					.isEqualTo("0:0");
		}

		@Test
		void withoutDefaults() throws Exception {
			assertThat(BiOptional.of(47, 11)
				.map((x, y) -> x + ":" + y)
				.orElse("0:0"))
					.isEqualTo("47:11");
			assertThat(BiOptional.ofNullable(47, 11)
				.map((x, y) -> x + ":" + y)
				.orElse("0:0"))
					.isEqualTo("47:11");
			assertThat(BiOptional.ofNullable(47, null)
				.map((x, y) -> x + ":" + y)
				.orElse("0:0"))
					.isEqualTo("0:0");
			assertThat(BiOptional.ofNullable(null, 11)
				.map((x, y) -> x + ":" + y)
				.orElse("0:0"))
					.isEqualTo("0:0");
			assertThat(BiOptional.ofNullable((Integer) null, (Integer) null)
				.map((x, y) -> x + ":" + y)
				.orElse("0:0"))
					.isEqualTo("0:0");
		}

		@Test
		void withGenericDefault() throws Exception {
			assertThat(BiOptional.of(47, 11)
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("47:11");
			assertThat(BiOptional.ofNullable(47, 11)
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("47:11");
			assertThat(BiOptional.ofNullable(47, null)
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("?:?");
			assertThat(BiOptional.ofNullable(null, 11)
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("?:?");
			assertThat(BiOptional.ofNullable((Integer) null, (Integer) null)
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("0:0");
		}

		@Test
		void testBimap() throws Exception {
			assertThat(BiOptional.of(47, 11)
				.bimap(x -> String.valueOf(x), y -> String.valueOf(y))
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("47:11");
			assertThat(BiOptional.of(47, 11)
				.bimap(x -> String.valueOf(x), y -> null)
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("?:?");
			assertThat(BiOptional.of(47, 11)
				.bimap(x -> null, y -> String.valueOf(y))
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("?:?");
			assertThat(BiOptional.of(47, 11)
				.bimap(x -> null, y -> null)
				.map((x, y) -> x + ":" + y, xy -> "?:?")
				.orElse("0:0"))
					.isEqualTo("0:0");
		}
	}

	@Test
	void testBiflatmap() throws Exception {
		assertThat(BiOptional.of(47, 11)
			.biflatmap(x -> Optional.of(x), y -> Optional.of(y))
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("47:11");
		assertThat(BiOptional.of(47, 11)
			.biflatmap(x -> Optional.of(x), y -> Optional.empty())
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("?:?");
		assertThat(BiOptional.of(47, 11)
			.biflatmap(x -> Optional.empty(), y -> Optional.of(y))
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("?:?");
		assertThat(BiOptional.of(47, 11)
			.biflatmap(x -> Optional.empty(), y -> Optional.empty())
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("0:0");
	}

	@Test
	void testBifilter() throws Exception {
		assertThat(BiOptional.of(47, 11)
			.bifilter(x -> true, y -> true)
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("47:11");
		assertThat(BiOptional.of(47, 11)
			.bifilter(x -> true, y -> false)
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("?:?");
		assertThat(BiOptional.of(47, 11)
			.bifilter(x -> false, y -> true)
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("?:?");
		assertThat(BiOptional.of(47, 11)
			.bifilter(x -> false, y -> false)
			.map((x, y) -> x + ":" + y, xy -> "?:?")
			.orElse("0:0"))
				.isEqualTo("0:0");
	}

}
