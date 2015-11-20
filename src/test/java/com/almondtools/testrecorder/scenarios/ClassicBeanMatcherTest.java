package com.almondtools.testrecorder.scenarios;

import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.testrecorder.CodeSerializer;
import com.almondtools.testrecorder.ConfigurableSerializerFacade;
import com.almondtools.testrecorder.profile.DefaultSerializationProfile;
import com.almondtools.testrecorder.visitors.ObjectToMatcherCode;

public class ClassicBeanMatcherTest {

	@Test
	public void testCodeSerializerSimple() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();
		
		assertThat(codeSerializer.serialize(createSimpleBean()), containsPattern(""
			+ "Matcher<ClassicBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Object o = null;*"
			+ "}.matching(ClassicBean.class)"));
	}

	@Test
	public void testCodeSerializerString() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();
		
		assertThat(codeSerializer.serialize(createStringBean()), containsPattern(""
			+ "Matcher<ClassicBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Object o = \"33\";*"
			+ "}.matching(ClassicBean.class);"));
	}

	@Test
	public void testCodeSerializerNested() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();
		
		assertThat(codeSerializer.serialize(createNestedBean()), containsPattern(""
			+ "Matcher<ClassicBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Matcher<ClassicBean> o = new GenericMatcher() {*"
			+ "int i = 0;*"
			+ "Object o = null;*"
			+ "}.matching(ClassicBean.class);*"
			+ "}.matching(ClassicBean.class);"));
	}

	@Test
	public void testCodeSerializerRecursive() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();
		
		assertThat(codeSerializer.serialize(createRecursiveBean()), containsPattern(""
			+ "Matcher<ClassicBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Matcher<ClassicBean> o = recursive(ClassicBean.class);*"
			+ "}.matching(ClassicBean.class);"));
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

	private static CodeSerializer matcherSerializer() {
		return new CodeSerializer(new ConfigurableSerializerFacade(new DefaultSerializationProfile()), new ObjectToMatcherCode.Factory());
	}

}