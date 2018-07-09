package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ExceptionalTest {

	@Test
	void testSuccess() throws Throwable {
		assertThat(Exceptional.success("String").andRecover("Default")).isEqualTo("String");
		assertThat(Exceptional.success("String").andRecover(t -> "Default")).isEqualTo("String");
		assertThat(Exceptional.success("String").orFail()).isEqualTo("String");
		assertThat(Exceptional.success("String").orFail(t -> new RuntimeException(t))).isEqualTo("String");
	}

	@Test
	void testThrowing() throws Throwable {
		assertThat(Exceptional.throwing(new IOException()).andRecover("Default")).isEqualTo("Default");
		assertThat(Exceptional.throwing(new IOException()).andRecover(t -> "Default")).isEqualTo("Default");
		assertThatThrownBy(() -> Exceptional.throwing(new IOException()).orFail()).isInstanceOf(IOException.class);
		assertThatThrownBy(() -> Exceptional.throwing(new IOException()).orFail(t -> new RuntimeException(t))).isInstanceOf(RuntimeException.class);;
	}

}
