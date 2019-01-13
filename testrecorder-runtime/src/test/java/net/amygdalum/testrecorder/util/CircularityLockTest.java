package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CircularityLockTest {

	private CircularityLock lock;

	@BeforeEach
	public void before() throws Exception {
		this.lock = new CircularityLock();
	}

	@Test
	void testCircularityLock() throws Exception {
		assertThat(lock.locked()).isFalse();
	}

	@Nested
	class testAquire {
		@Test
		void onFree() throws Exception {
			boolean aquired = lock.acquire();

			assertThat(aquired).isTrue();
			assertThat(lock.locked()).isTrue();
		}

		@Test
		void onAquired() throws Exception {
			lock.acquire();

			boolean aquired = lock.acquire();

			assertThat(aquired).isFalse();
			assertThat(lock.locked()).isTrue();
		}
	}

	@Test
	void testRelease() throws Exception {
		lock.acquire();

		lock.release();

		assertThat(lock.locked()).isFalse();
	}

}
