package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Simple;

public class ClassesByPackageTest {

	@Nested
	class testMatches {
		@Test
		public void onReflectiveClass() throws Exception {
			assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.testobjects").matches(Simple.class)).isTrue();
			assertThat(Classes.byPackage("net.amygdalum.testrecorder.util").matches(Simple.class)).isTrue();
			assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.scenarios").matches(Simple.class)).isFalse();
		}

		@Test
		public void onClassDescriptor() throws Exception {
			assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.testobjects").matches("net/amygdalum/testrecorder/util/testobjects/Simple")).isTrue();
			assertThat(Classes.byPackage("net.amygdalum.testrecorder.util").matches("net/amygdalum/testrecorder/util/testobjects/Simple")).isTrue();
			assertThat(Classes.byPackage("net.amygdalum.testrecorder.util.scenarios").matches("net/amygdalum/testrecorder/util/testobjects/Simple")).isFalse();
		}
	}
}
