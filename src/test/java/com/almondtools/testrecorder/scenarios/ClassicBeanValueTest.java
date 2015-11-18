package com.almondtools.testrecorder.scenarios;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.testrecorder.CodeSerializer;

public class ClassicBeanValueTest {

	@Test
	public void testCodeSerializerSimple() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createSimpleBean()), allOf(
			containsString("classicBean1 = new ClassicBean()"), 
			containsString("classicBean1.setI(22)"), 
			not(containsString("classicBean1.setO"))));
	}

	@Test
	public void testCodeSerializerString() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createStringBean()), allOf(
			containsString("classicBean1 = new ClassicBean()"), 
			containsString("classicBean1.setI(22)"), 
			containsString("classicBean1.setO(\"33\")")));
	}

	@Test
	public void testCodeSerializerNested() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createNestedBean()), allOf(
			containsString("classicBean1 = new ClassicBean()"), 
			containsString("classicBean1.setI(22)"), 
			containsString("classicBean1.setO(classicBean2)")));
	}

	@Test
	public void testCodeSerializerRecursive() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();
		
		assertThat(codeSerializer.serialize(createRecursiveBean()), allOf(
			containsString("classicBean1 = new ClassicBean()"), 
			containsString("classicBean1.setI(22)"), 
			containsString("classicBean1.setO(classicBean1)")));
	}

	private static ClassicBean createSimpleBean() {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO(null);
		return bean;
	}

	private static ClassicBean createStringBean() {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO("33");
		return bean;
	}

	private static ClassicBean createNestedBean() {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO(new ClassicBean());
		return bean;
	}

	private static ClassicBean createRecursiveBean() {
		ClassicBean bean = new ClassicBean();
		bean.setI(22);
		bean.setO(bean);
		return bean;
	}

}