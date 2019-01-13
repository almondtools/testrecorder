package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.ClasspathResourceExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class WrappedTest {

	@Nested
	class testClazz {
		@Test
		void onClass() throws Exception {
			Wrapped wrapped = Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.Simple");

			assertThat(wrapped.getWrappedClass()).isSameAs(Simple.class);
			assertThat(wrapped.value()).isInstanceOf(Simple.class);
		}

		@Test
		void onInterface() throws Exception {
			assertThatThrownBy(() -> Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.NonGenericInterface"))
				.isInstanceOf(GenericObjectException.class);
		}

		@Test
		void onEnum() throws Exception {
			assertThatThrownBy(() -> Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.EmptyEnum"))
				.isInstanceOf(GenericObjectException.class);
		}
	}

	@Nested
	class testEnumType {
		@Test
		void onEnum() throws Exception {
			Wrapped wrapped = Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.PublicEnum", "VALUE1");

			assertThat(PublicEnum.class).isAssignableFrom(wrapped.getWrappedClass());
			assertThat(wrapped.value()).isEqualTo(PublicEnum.VALUE1);
		}

		void onNotExistingEnum() throws Exception {
			assertThatThrownBy(() -> Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.EmptyEnum", "VALUE1"))
				.isInstanceOf(GenericObjectException.class);
		}

		@Test
		void onClass() throws Exception {
			assertThatThrownBy(() -> Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.Simple", ""))
				.isInstanceOf(GenericObjectException.class);
		}

		@Test
		void onInterface() throws Exception {
			assertThatThrownBy(() -> Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.NonGenericInterface", ""))
				.isInstanceOf(GenericObjectException.class);
		}
	}

	@Test
	void testSetField() throws Exception {
		Wrapped wrapped = Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.Simple");
		wrapped.setField("str", "new value");

		assertThat(wrapped.value()).satisfies(value -> assertThat(((Simple) value).getStr()).isEqualTo("new value"));
	}

	@Nested
	class testClassForName {
		@Test
		void onCommon() throws Exception {
			Thread.currentThread().setContextClassLoader(WrappedTest.class.getClassLoader());

			assertThat(Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.Simple")).isSameAs(Simple.class);
		}

		@Test
		void onNotExisting() throws Exception {
			Thread.currentThread().setContextClassLoader(WrappedTest.class.getClassLoader());

			assertThatThrownBy(() -> Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.NotExisting")).isInstanceOf(GenericObjectException.class);
		}

		@Test
		void withoutContextClassLoader() throws Exception {
			Thread.currentThread().setContextClassLoader(null);
			assertThat(Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.Simple")).isSameAs(Simple.class);
		}

		@Test
		@ExtendWith(ClasspathResourceExtension.class)
		void withRedefiningContextClassLoader(ExtensibleClassLoader classLoader) throws Exception {
			classLoader.redefineClass("net.amygdalum.testrecorder.util.testobjects.Simple");

			Class<?> classForName = Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.Simple");

			assertThat(classForName).isNotSameAs(Simple.class);
			assertThat(classForName.getName()).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Simple");
		}
	}
}
