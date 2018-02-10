package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class MethodsByNameTest {

	private MethodsByName methodByName;

	@BeforeEach
	public void before() throws Exception {
		methodByName = new MethodsByName("getStr");
	}

	@Test
	public void testFactoryMethod() throws Exception {
		assertThat(methodByName).isEqualToComparingFieldByField(Methods.byName("getStr"));
	}

	@Test
	public void testMatchesReflectiveMethod() throws Exception {
		assertThat(methodByName.matches(Simple.class.getDeclaredMethod("getStr"))).isTrue();
		assertThat(methodByName.matches(SimpleMisleadingFieldName.class.getDeclaredMethod("getStr"))).isTrue();
		assertThat(methodByName.matches(Complex.class.getDeclaredMethod("getSimple"))).isFalse();
	}

	@Test
	public void testMatchesMethodDescriptor() throws Exception {
		assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "getStr", "()Ljava/lang/String;")).isTrue();
		assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "getStr", "()Ljava/lang/String;")).isTrue();
		assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "getSimple", "()Lnet/amygdalum/testrecorder/util/testobjects/Simple;")).isFalse();
	}

}
