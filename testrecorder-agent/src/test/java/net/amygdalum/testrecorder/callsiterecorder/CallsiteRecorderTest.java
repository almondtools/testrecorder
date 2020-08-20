package net.amygdalum.testrecorder.callsiterecorder;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.util.ContextClassloaderExtension;

@ExtendWith(ContextClassloaderExtension.class)
public class CallsiteRecorderTest {

	@Nested
	class testRecord {
		@Test
		void onRunnable() throws Exception {
			Example example = new Example(2);

			try (CallsiteRecorder recorder = CallsiteRecorder.create(method("reset"))) {
				List<ContextSnapshot> recorded = recorder.record(() -> example.reset()).join();

				assertThat(recorded).hasSize(1);
			}
		}

		@Test
		void onCallable() throws Exception {
			Example example = new Example(0);

			try (CallsiteRecorder recorder = CallsiteRecorder.create(method("inc"))) {

				int value = recorder.record(() -> example.inc());

				List<ContextSnapshot> recorded = recorder.snapshots().join();

				assertThat(value).isEqualTo(1);
				assertThat(recorded).hasSize(1);
			}
		}
	}

	private Method method(String name, Class<?>... params) {
		try {
			return Example.class.getDeclaredMethod(name, params);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static class Example {
		private int i;

		public Example(int i) {
			this.i = i;
		}

		public int inc() {
			return ++i;
		}

		public void reset() {
			i = 0;
		}
	}

}
