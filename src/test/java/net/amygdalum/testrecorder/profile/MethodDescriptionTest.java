package net.amygdalum.testrecorder.profile;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.Methods;
import net.amygdalum.testrecorder.util.testobjects.Collections;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class MethodDescriptionTest {

	private Methods methodByDescriptionResult;
	private Methods methodByDescriptionArguments;

	@Before
	public void before() throws Exception {
		methodByDescriptionResult = Methods.byDescription("net/amygdalum/testrecorder/util/testobjects/Simple", "getStr", "()Ljava/lang/String;");
		methodByDescriptionArguments = Methods.byDescription("net/amygdalum/testrecorder/util/testobjects/Collections", "arrayList", "([Ljava/lang/Object;)Ljava/util/ArrayList;");
	}
	
	@Test
	public void testMatchesReflectiveMethod() throws Exception {
		assertThat(methodByDescriptionResult.matches(Simple.class.getDeclaredMethod("getStr")), is(true)); 
		assertThat(methodByDescriptionResult.matches(SimpleMisleadingFieldName.class.getDeclaredMethod("getStr")), is(false)); 

		assertThat(methodByDescriptionArguments.matches(Collections.class.getDeclaredMethod("arrayList", Object[].class)), is(true)); 
		assertThat(methodByDescriptionArguments.matches(Simple.class.getDeclaredMethod("getStr")), is(false)); 
	}

	@Test
	public void testMatchesMethodDescriptor() throws Exception {
		assertThat(methodByDescriptionResult.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "getStr", "()Ljava/lang/String;"), is(true)); 
		assertThat(methodByDescriptionResult.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "getStr", "()Ljava/lang/String;"), is(false)); 

		assertThat(methodByDescriptionArguments.matches("net/amygdalum/testrecorder/util/testobjects/Collections", "arrayList", "([Ljava/lang/Object;)Ljava/util/ArrayList;"), is(true)); 
		assertThat(methodByDescriptionArguments.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "getStr", "()Ljava/lang/String;"), is(false)); 
	}

}
