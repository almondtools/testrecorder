package net.amygdalum.testrecorder.fakeio;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.fakeio.FakeIO.SelfSpecification;

public class FakeIOTest {

	@Test
	void testAnyInvocation() throws Exception {
		SelfSpecification invocation = new FakeIO.AnyInvocation();

		assertThat(invocation.matches("String")).isTrue();
	}

	@Test
	void testStaticInvocation() throws Exception {
		SelfSpecification invocation = new FakeIO.StaticInvocation();

		assertThat(invocation.matches("String")).isFalse();
		assertThat(invocation.matches(null)).isTrue();
	}

	@Test
	void testBoundInvocation() throws Exception {
		String bound = new String("String");
		
		SelfSpecification invocation = new FakeIO.BoundInvocation(bound);
		
		assertThat(invocation.matches(bound)).isTrue();
		assertThat(invocation.matches("String")).isFalse();
		assertThat(invocation.matches(null)).isFalse();
	}
	
	@Test
	void testBindableInvocation() throws Exception {
		SelfSpecification invocation = new FakeIO.BindableInvocation();
		
		String instance = "String";

		assertThat(invocation.matches(instance)).isTrue();
		assertThat(invocation.matches(instance)).isTrue();
		
		assertThat(invocation.matches(null)).isFalse();
	}
	
}
