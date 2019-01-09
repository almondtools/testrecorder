package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.ClassDescriptor;

public class ClassDescriptorTest {

	@Test
	public void testEquals() throws Exception {
		assertThat(ClassDescriptor.of(String.class)).satisfies(defaultEquality()
			.andNotEqualTo(ClassDescriptor.of(Integer.class))
			.andEqualTo(ClassDescriptor.of(String.class))
			.conventions());
	}

	@Test
	public void testGetPackage() throws Exception {
		assertThat(ClassDescriptor.of(String.class).getPackage()).isEqualTo("java.lang");
	}

	@Test
	public void testGetSimpleName() throws Exception {
		assertThat(ClassDescriptor.of(String.class).getSimpleName()).isEqualTo("String");
	}

}
