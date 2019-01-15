package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class MethodsByNameTest {

	@Nested
	class testMethodsByName {

		@Test
		public void defaultConstruction() throws Exception {
			MethodsByName methodByName = new MethodsByName("getStr");
			assertThat(methodByName).isEqualToComparingFieldByField(Methods.byName("getStr"));
		}

		@Test
		public void rejectedConstruction() throws Exception {
			assertThatCode(() -> new MethodsByName("getStr()")).isInstanceOf(IllegalArgumentException.class);
			assertThatCode(() -> new MethodsByName("String getStr")).isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Nested
	class testMatches {
		@Test
		public void onReflectiveMethod() throws Exception {
			MethodsByName methodByName = new MethodsByName("getStr");
			assertThat(methodByName.matches(Simple.class.getDeclaredMethod("getStr"))).isTrue();
			assertThat(methodByName.matches(SimpleMisleadingFieldName.class.getDeclaredMethod("getStr"))).isTrue();
			assertThat(methodByName.matches(Complex.class.getDeclaredMethod("getSimple"))).isFalse();
		}

		@Test
		public void onMethodByQualifiedName() throws Exception {
			MethodsByName methodByName = new MethodsByName("net.amygdalum.testrecorder.util.testobjects.Simple.getStr");
			assertThat(methodByName.matches(Simple.class.getDeclaredMethod("getStr"))).isTrue();
			assertThat(methodByName.matches(SimpleMisleadingFieldName.class.getDeclaredMethod("getStr"))).isFalse();
			assertThat(methodByName.matches(Complex.class.getDeclaredMethod("getSimple"))).isFalse();
		}

		@Test
		public void onMethodDescriptor() throws Exception {
			MethodsByName methodByName = new MethodsByName("getStr");
			assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "getStr", "()Ljava/lang/String;")).isTrue();
			assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "getStr", "()Ljava/lang/String;")).isTrue();
			assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "getSimple", "()Lnet/amygdalum/testrecorder/util/testobjects/Simple;")).isFalse();
		}

		@Test
		public void onMethodDescriptorByQualifiedName() throws Exception {
			MethodsByName methodByName = new MethodsByName("net.amygdalum.testrecorder.util.testobjects.Simple.getStr");
			assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "getStr", "()Ljava/lang/String;")).isTrue();
			assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "getStr", "()Ljava/lang/String;")).isFalse();
			assertThat(methodByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "getSimple", "()Lnet/amygdalum/testrecorder/util/testobjects/Simple;")).isFalse();
		}
	}
}
