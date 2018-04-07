package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class ClassDescriptionTest {

	private ClassDescription classByDescription;

	@BeforeEach
	public void before() {
		classByDescription = new ClassDescription("net/amygdalum/testrecorder/util/testobjects/Simple");
	}
	
	@Test
	public void testAlternativeFactories() throws Exception {
		assertThat(classByDescription).isEqualToComparingFieldByField(Classes.byDescription("net/amygdalum/testrecorder/util/testobjects/Simple"));
		assertThat(classByDescription).isEqualToComparingFieldByField(Classes.byDescription(Simple.class));
	}
	
	@Test
	public void testMatchesReflectiveClass() throws Exception {
		assertThat(classByDescription.matches(Simple.class)).isTrue(); 
		assertThat(classByDescription.matches(SimpleMisleadingFieldName.class)).isFalse(); 
		assertThat(classByDescription.matches(Complex.class)).isFalse(); 
	}

	@Test
	public void testMatchesClassDescriptor() throws Exception {
		assertThat(classByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Simple")).isTrue(); 
		assertThat(classByDescription.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName")).isFalse(); 
		assertThat(classByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Complex")).isFalse(); 
	}

}
