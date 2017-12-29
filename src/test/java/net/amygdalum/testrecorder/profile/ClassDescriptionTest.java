package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class ClassDescriptionTest {

	private Classes classByDescription;

	@BeforeEach
	public void before() {
		classByDescription = Classes.byDescription("net/amygdalum/testrecorder/util/testobjects/Simple");
	}
	
	@Test
	public void testMatchesReflectiveClass() throws Exception {
		assertThat(classByDescription.matches(Simple.class), is(true)); 
		assertThat(classByDescription.matches(SimpleMisleadingFieldName.class), is(false)); 
		assertThat(classByDescription.matches(Complex.class), is(false)); 
	}

	@Test
	public void testMatchesClassDescriptor() throws Exception {
		assertThat(classByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Simple"), is(true)); 
		assertThat(classByDescription.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName"), is(false)); 
		assertThat(classByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Complex"), is(false)); 
	}

}
