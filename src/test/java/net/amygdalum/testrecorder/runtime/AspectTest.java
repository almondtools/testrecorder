package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

public class AspectTest {

	@Test
	public void testAspectCorrectInstantiation() throws Exception {
		Aspect aspect = new Aspect() {
			@SuppressWarnings("unused")
			void method() {
			}
		};
		
		assertThat(aspect.getName()).isEqualTo("method");
		assertThat(aspect.getDesc()).isEqualTo("()V");
	}

	@Test
	public void testAspectNotUnique() throws Exception {
		assertThatCode(() -> new Aspect() {
		}).isInstanceOf(IllegalArgumentException.class);
		assertThatCode(() -> new Aspect() {
			@SuppressWarnings("unused")
			void method1() {
			}
			@SuppressWarnings("unused")
			void method2() {
			}
		}).isInstanceOf(IllegalArgumentException.class);
	}

}
