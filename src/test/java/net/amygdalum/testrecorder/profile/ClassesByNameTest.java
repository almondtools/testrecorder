package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class ClassesByNameTest {

	private Classes classByName;

	@Before
	public void before() {
		classByName = Classes.byName("net.amygdalum.testrecorder.util.testobjects.Simple");
	}
	
	@Test
	public void testMatchesReflectiveClass() throws Exception {
		assertThat(classByName.matches(Simple.class), is(true)); 
		assertThat(classByName.matches(SimpleMisleadingFieldName.class), is(false)); 
		assertThat(classByName.matches(Complex.class), is(false)); 
	}

	@Test
	public void testMatchesClassDescriptor() throws Exception {
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple"), is(true)); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName"), is(false)); 
		assertThat(classByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex"), is(false)); 
	}

}
