package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class WrappedTest {

	@Test
	public void testClazzOnClass() throws Exception {
		Wrapped wrapped = Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.Simple");
		
		assertThat(wrapped.getWrappedClass()).isSameAs(Simple.class);
		assertThat(wrapped.value()).isInstanceOf(Simple.class);
	}

	@Test
	public void testClazzOnInterface() throws Exception {
		assertThatThrownBy(() -> Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.NonGenericInterface"))
			.isInstanceOf(GenericObjectException.class);
	}

	@Test
	public void testClazzOnEnum() throws Exception {
		assertThatThrownBy(() -> Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.EmptyEnum"))
			.isInstanceOf(GenericObjectException.class);
	}

	@Test
	public void testEnumTypeOnEnum() throws Exception {
		Wrapped wrapped = Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.PublicEnum", "VALUE1");

		assertThat(PublicEnum.class).isAssignableFrom(wrapped.getWrappedClass());
		assertThat(wrapped.value()).isEqualTo(PublicEnum.VALUE1);
	}

	public void testEnumTypeOnNotExistingEnum() throws Exception {
		assertThatThrownBy(() -> Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.EmptyEnum", "VALUE1"))
			.isInstanceOf(GenericObjectException.class);
	}

	@Test
	public void testEnumTypeOnClass() throws Exception {
		assertThatThrownBy(() -> Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.Simple", ""))
			.isInstanceOf(GenericObjectException.class);
	}

	@Test
	public void testEnumTypeOnInterface() throws Exception {
		assertThatThrownBy(() -> Wrapped.enumType("net.amygdalum.testrecorder.util.testobjects.NonGenericInterface", ""))
			.isInstanceOf(GenericObjectException.class);
	}

	@Test
	public void testSetField() throws Exception {
		Wrapped wrapped = Wrapped.clazz("net.amygdalum.testrecorder.util.testobjects.Simple");
		wrapped.setField("str", "new value");
		
		assertThat(wrapped.value()).satisfies(value -> assertThat(((Simple) value).getStr()).isEqualTo("new value"));
	}

	@Test
	public void testClassForName() throws Exception {
		Thread.currentThread().setContextClassLoader(WrappedTest.class.getClassLoader());
		
		assertThat(Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.Simple")).isSameAs(Simple.class);
	}

	@Test
	public void testClassForNameNotExisting() throws Exception {
		Thread.currentThread().setContextClassLoader(WrappedTest.class.getClassLoader());

		assertThatThrownBy(() -> Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.NotExisting")).isInstanceOf(GenericObjectException.class);
	}

	@Test
	public void testClassForNameWithoutContextClassLoader() throws Exception {
		Thread.currentThread().setContextClassLoader(null);
		assertThat(Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.Simple")).isSameAs(Simple.class);
	}

	@Test
	public void testClassForNameWithRedefiningContextClassLoader() throws Exception {
		ExtensibleClassLoader classLoader = new ExtensibleClassLoader(WrappedTest.class.getClassLoader());
		classLoader.redefineClass("net.amygdalum.testrecorder.util.testobjects.Simple");
		Thread.currentThread().setContextClassLoader(classLoader);
		
		Class<?> classForName = Wrapped.classForName("net.amygdalum.testrecorder.util.testobjects.Simple");
		
		assertThat(classForName).isNotSameAs(Simple.class);
		assertThat(classForName.getName()).isEqualTo("net.amygdalum.testrecorder.util.testobjects.Simple");
	}

}
