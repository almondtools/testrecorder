package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class BiOptionalTest {

	@Test
	void testMapWithFunctionalDefaults() throws Exception {
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
	void testMapWithoutDefaults() throws Exception {
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
	void testMapWithGenericDefault() throws Exception {
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


}
