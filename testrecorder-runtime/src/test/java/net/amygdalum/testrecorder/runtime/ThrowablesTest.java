package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ThrowablesTest {

	@Test
	void testThrowables() throws Exception {
		assertThat(Throwables.class).satisfies(utilityClass().conventions());
	}

	@Nested
	class testCapture {
		@Test
		void withoutResult() throws Exception {
			Throwable capture = Throwables.capture(() -> {
				try {
					throw new IllegalArgumentException("captured");
				} catch (NullPointerException e) {
					return;
				}
			});
			assertThat(capture)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("captured");
		}

		@Test
		void withoutResultUnexpected() throws Exception {
			Throwable capture = Throwables.capture(() -> {
			});
			assertThat(capture).isNull();
		}

		@Test
		void typedWithoutResult() throws Exception {
			Throwable capture = Throwables.capture(() -> {
				try {
					throw new IllegalArgumentException("captured");
				} catch (NullPointerException e) {
					return;
				}
			}, IllegalArgumentException.class);
			assertThat(capture)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("captured");
		}

		@Test
		void typedWithoutResultFailing() throws Exception {
			Throwable capture = Throwables.capture(() -> {
				try {
					throw new ArrayIndexOutOfBoundsException("captured");
				} catch (NullPointerException e) {
					return;
				}
			}, IllegalArgumentException.class);
			assertThat(capture).isNull();
		}

		@Test
		void withResult() throws Exception {
			Throwable capture = Throwables.capture(() -> {
				try {
					throw new IllegalArgumentException("captured");
				} catch (NullPointerException e) {
					return "success";
				}
			});
			assertThat(capture)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("captured");
		}

		@Test
		void withResultUnexpected() throws Exception {
			Throwable capture = Throwables.capture(() -> {
				return "success";
			});
			assertThat(capture).isNull();
		}

		@Test
		void typedWithResult() throws Exception {
			Throwable capture = Throwables.capture(() -> {
				try {
					throw new IllegalArgumentException("captured");
				} catch (NullPointerException e) {
					return "success";
				}
			}, IllegalArgumentException.class);
			assertThat(capture)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("captured");
		}

		@Test
		void withResultFailing() throws Exception {
			Throwable capture = Throwables.capture(() -> {
				try {
					throw new ArrayIndexOutOfBoundsException("captured");
				} catch (NullPointerException e) {
					return "success";
				}
			}, IllegalArgumentException.class);
			assertThat(capture).isNull();
		}
	}
}
