package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class OptionalValueTest {

	@Test
	void testOptionalValue() throws Exception {
		assertThat(OptionalValue.of("String").orElseThrow(RuntimeException::new)).isEqualTo("String");
		assertThat(OptionalValue.of(null).orElseThrow(RuntimeException::new)).isNull();
		assertThat(OptionalValue.empty().orElse("Default")).isEqualTo("Default");
		assertThat(OptionalValue.of(Optional.of("String")).orElse("Default")).isEqualTo("String");
		assertThat(OptionalValue.of(Optional.empty()).orElse("Default")).isEqualTo("Default");
	}

	@Test
	void testFilter() throws Exception {
		assertThat(OptionalValue.of("String").filter(s -> s.startsWith("Str")).orElse("Default")).isEqualTo("String");
		assertThat(OptionalValue.empty().filter(s -> true).orElse("Default")).isEqualTo("Default");
		assertThat(OptionalValue.of("String").filter(s -> s.startsWith("Ch")).orElse("Default")).isEqualTo("Default");
	}

	@Test
	void testMap() throws Exception {
		assertThat(OptionalValue.of("String").map(s -> s.substring(0, 3)).orElse("Default")).isEqualTo("Str");
		assertThat(OptionalValue.of("String").map(s -> (String) null).orElse("Default")).isNull();
		assertThat(OptionalValue.empty().map(s -> s).orElse("Default")).isEqualTo("Default");
	}

	@Test
	void testFlatMap() throws Exception {
		assertThat(OptionalValue.of("String").flatMap(s -> OptionalValue.of(s)).orElse("Default")).isEqualTo("String");
		assertThat(OptionalValue.of("String").flatMap(s -> OptionalValue.empty()).orElse("Default")).isEqualTo("Default");
		assertThat(OptionalValue.empty().flatMap(s -> OptionalValue.of(s)).orElse("Default")).isEqualTo("Default");
	}

	@Test
	void testOrElseGet() throws Exception {
		assertThat(OptionalValue.of("String").orElseGet(() -> "Default")).isEqualTo("String");
		assertThat(OptionalValue.empty().orElseGet(() -> "Default")).isEqualTo("Default");
	}

	@Test
	void testOrElseThrow() throws Exception {
		assertThat(OptionalValue.of("String").orElseThrow(RuntimeException::new)).isEqualTo("String");
		assertThatThrownBy(() -> OptionalValue.empty().orElseThrow(RuntimeException::new)).isInstanceOf(RuntimeException.class);
	}

	@Test
	void testIfPresent() throws Exception {
		Object[] result = new Object[1];
		OptionalValue.of("String").ifPresent(s -> result[0] = s);

		assertThat(result[0]).isEqualTo("String");
	}

	@Test
	void testIfPresentFalse() throws Exception {
		Object[] result = new Object[1];
		OptionalValue.empty().ifPresent(s -> result[0] = s);

		assertThat(result[0]).isNull();
	}

	@Test
	void testIfPresentOrElse() throws Exception {
		Object[] result = new Object[1];
		OptionalValue.of("String").ifPresentOrElse(s -> result[0] = s, () -> result[0] = "Default");

		assertThat(result[0]).isEqualTo("String");
	}

	@Test
	void testIfPresentOrElseFalse() throws Exception {
		Object[] result = new Object[1];
		OptionalValue.empty().ifPresentOrElse(s -> result[0] = s, () -> result[0] = "Default");

		assertThat(result[0]).isEqualTo("Default");
	}

	@Test
	void testStream() throws Exception {
		assertThat(OptionalValue.of("String").stream()).contains("String");
		assertThat(OptionalValue.empty().stream()).isEmpty();

	}
}
