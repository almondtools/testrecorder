package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class ClassesByNameTest {

	@Test
	public void testFactoryMethod() throws Exception {
		ClassesByName classByName = new ClassesByName("net.amygdalum.testrecorder.util.testobjects.Simple");
		assertThat(classByName).isEqualToComparingFieldByField(Classes.byName("net.amygdalum.testrecorder.util.testobjects.Simple"));
	}
	
	@Test
	public void testFactoryMethodRejection() throws Exception {
		assertThatCode(() -> new ClassesByName("String;")).isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testMatchesReflectiveClass() throws Exception {
		ClassesByName classByName = new ClassesByName("net.amygdalum.testrecorder.util.testobjects.Simple");
		assertThat(classByName.matches(Simple.class)).isTrue(); 
		assertThat(classByName.matches(SimpleMisleadingFieldName.class)).isFalse(); 
		assertThat(classByName.matches(Complex.class)).isFalse(); 
		assertThat(classByName.matches(net.amygdalum.testrecorder.profile.Simple.class)).isFalse(); 
	}

	@Test
	public void testMatchesReflectiveClassBySimpleName() throws Exception {
		ClassesByName classByName = new ClassesByName("Simple");
		assertThat(classByName.matches(Simple.class)).isTrue(); 
		assertThat(classByName.matches(SimpleMisleadingFieldName.class)).isFalse(); 
		assertThat(classByName.matches(net.amygdalum.testrecorder.profile.Simple.class)).isTrue(); 
	}

	@Test
	public void testMatchesClassDescriptor() throws Exception {
		ClassesByName classByName = new ClassesByName("net.amygdalum.testrecorder.util.testobjects.Simple");
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple")).isTrue(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName")).isFalse(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex")).isFalse(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/profile/Simple")).isFalse(); 
	}

	@Test
	public void testMatchesClassDescriptorBySimpleName() throws Exception {
		ClassesByName classByName = new ClassesByName("Simple");
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple")).isTrue(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName")).isFalse(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex")).isFalse(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/profile/Simple")).isTrue(); 
	}

}
