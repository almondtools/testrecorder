package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;
import net.amygdalum.testrecorder.util.testobjects.SimpleNoDefaultConstructor;

public class FieldsByNameTest {

	private Fields fieldByName;

	@BeforeEach
	public void before() {
		fieldByName = Fields.byName("str");
	}
	
	@Test
	public void testMatchesReflectiveField() throws Exception {
		assertThat(fieldByName.matches(Simple.class.getDeclaredField("str")), is(true)); 
		assertThat(fieldByName.matches(SimpleMisleadingFieldName.class.getDeclaredField("str")), is(true)); 
		assertThat(fieldByName.matches(SimpleNoDefaultConstructor.class.getDeclaredField("str")), is(true)); 
		assertThat(fieldByName.matches(Complex.class.getDeclaredField("simple")), is(false)); 
	}

	@Test
	public void testMatchesFieldDescriptor() throws Exception {
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;"), is(true)); 
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "str", "I"), is(true)); 
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleNoDefaultConstructor", "str", "Ljava/lang/String;"), is(true)); 
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "simple", "Lnet/amygdalum/testrecorder/util/testobjects/Simple;"), is(false)); 
	}

}
