package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ThrowablesTest {

	@Test
	public void testThrowables() throws Exception {
		assertThat(Throwables.class).satisfies(utilityClass().conventions());
	}

	@Test
	public void testCaptureWithoutResult() throws Exception {
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
	public void testCaptureWithoutResultUnexpected() throws Exception {
		Throwable capture = Throwables.capture(() -> {
		});
		assertThat(capture).isNull();
	}

	@Test
	public void testSpecificCaptureWithoutResult() throws Exception {
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
	public void testUnexpectedCaptureWithoutResult() throws Exception {
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
	public void testCaptureWithResult() throws Exception {
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
	public void testCaptureWithResultUnexpected() throws Exception {
		Throwable capture = Throwables.capture(() -> {
			return "success";
		});
		assertThat(capture).isNull();
	}

	@Test
	public void testSpecificCaptureWithResult() throws Exception {
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
	public void testUnexpectedCaptureWithResult() throws Exception {
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
