package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class ClassesByNameTest {

	private ClassesByName classByName;

	@BeforeEach
	public void before() {
		classByName = new ClassesByName("net.amygdalum.testrecorder.util.testobjects.Simple");
	}
	
	@Test
	public void testFactoryMethod() throws Exception {
		assertThat(classByName).isEqualToComparingFieldByField(Classes.byName("net.amygdalum.testrecorder.util.testobjects.Simple"));
	}
	
	@Test
	public void testMatchesReflectiveClass() throws Exception {
		assertThat(classByName.matches(Simple.class)).isTrue(); 
		assertThat(classByName.matches(SimpleMisleadingFieldName.class)).isFalse(); 
		assertThat(classByName.matches(Complex.class)).isFalse(); 
	}

	@Test
	public void testMatchesClassDescriptor() throws Exception {
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple")).isTrue(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName")).isFalse(); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex")).isFalse(); 
	}

}
