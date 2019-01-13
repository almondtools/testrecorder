package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class OptionalsTest {

	@Test
	void testOptionals() throws Exception {
		assertThat(Optionals.class).satisfies(utilityClass().conventions());
	}

	@Test
	void testStream() throws Exception {
		assertThat(Optionals.stream(Optional.of("element")).count()).isEqualTo(1);
		assertThat(Optionals.stream(Optional.empty()).count()).isEqualTo(0);
	}

}
