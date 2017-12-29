package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class FieldDescriptionTest {

	private Fields fieldByDescription;

	@BeforeEach
	public void before() {
		fieldByDescription = Fields.byDescription("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;");
	}
	
	@Test
	public void testMatchesReflectiveField() throws Exception {
		assertThat(fieldByDescription.matches(Simple.class.getDeclaredField("str")), is(true)); 
		assertThat(fieldByDescription.matches(SimpleMisleadingFieldName.class.getDeclaredField("str")), is(false)); 
		assertThat(fieldByDescription.matches(Complex.class.getDeclaredField("simple")), is(false)); 
	}

	@Test
	public void testMatchesFieldDescriptor() throws Exception {
		assertThat(fieldByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;"), is(true)); 
		assertThat(fieldByDescription.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "str", "I"), is(false)); 
		assertThat(fieldByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "simple", "Lnet/amygdalum/testrecorder/util/testobjects/Simple;"), is(false)); 
	}

}
