package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEnum.defaultEnum;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LocalVariableTest {

	@Nested
	class testLocalVariable {
		@Test
		void initially() throws Exception {
			LocalVariable var = new LocalVariable("var", Object.class);

			assertThat(var.getName()).isEqualTo("var");
			assertThat(var.getType()).isEqualTo(Object.class);
		}

		@Test
		void allocated() throws Exception {
			LocalVariable i = new LocalVariable("i");
			assertThat(i.isDefined()).isFalse();
			assertThat(i.isReady()).isFalse();

			LocalVariable iTyped = new LocalVariable("i", Integer.class);
			assertThat(iTyped.isDefined()).isFalse();
			assertThat(iTyped.isReady()).isFalse();
		}

		@Test
		void afterDefinition() throws Exception {
			LocalVariable define = new LocalVariable("i")
				.define(Integer.class);

			assertThat(define.isDefined()).isTrue();
			assertThat(define.isReady()).isFalse();
		}

		@Test
		void afterFinish() throws Exception {
			LocalVariable define = new LocalVariable("i")
				.define(Integer.class)
				.finish();

			assertThat(define.isDefined()).isTrue();
			assertThat(define.isReady()).isTrue();
		}
	}

	@Nested
	class testProgress {
		@Test
		void isEnum() throws Exception {
			Class<?> progress = Arrays.stream(LocalVariable.class.getDeclaredClasses())
				.filter(clazz -> clazz.isEnum())
				.findFirst().orElse(null);

			assertThat(progress).satisfies(defaultEnum().withElements(3).conventions());
		}
	}

}
