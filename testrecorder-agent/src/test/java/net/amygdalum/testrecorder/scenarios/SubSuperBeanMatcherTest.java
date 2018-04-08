package net.amygdalum.testrecorder.scenarios;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.codeserializer.CodeSerializer;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerators;

public class SubSuperBeanMatcherTest {

	@Test
	public void testCodeSerializerSimple() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();

		assertThat(codeSerializer.serialize(createSimpleBean())).containsWildcardPattern(""
			+ "Matcher<SubBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Object o = null;*"
			+ "}.matching(SubBean.class)");
	}

	@Test
	public void testCodeSerializerString() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();

		assertThat(codeSerializer.serialize(createStringBean())).containsWildcardPattern(""
			+ "Matcher<SubBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Object o = \"33\";*"
			+ "}.matching(SubBean.class);");
	}

	@Test
	public void testCodeSerializerNested() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();

		assertThat(codeSerializer.serialize(createNestedBean())).containsWildcardPattern(""
			+ "Matcher<SubBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Matcher<?> o = new GenericMatcher() {*"
			+ "int i = 0;*"
			+ "Object o = null;*"
			+ "}.matching(SubBean.class, Object.class);*"
			+ "}.matching(SubBean.class);");
	}

	@Test
	public void testCodeSerializerRecursive() throws Exception {
		CodeSerializer codeSerializer = matcherSerializer();

		assertThat(codeSerializer.serialize(createRecursiveBean())).containsWildcardPattern(""
			+ "Matcher<SubBean> serializedObject1 = new GenericMatcher() {*"
			+ "int i = 22;*"
			+ "Matcher<?> o = recursive(SubBean.class);*"
			+ "}.matching(SubBean.class);");
	}

	private static SubBean createSimpleBean() {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO(null);
		return bean;
	}

	private static SubBean createStringBean() {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO("33");
		return bean;
	}

	private static SubBean createNestedBean() {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO(new SubBean());
		return bean;
	}

	private static SubBean createRecursiveBean() {
		SubBean bean = new SubBean();
		bean.setI(22);
		bean.setO(bean);
		return bean;
	}

	private static CodeSerializer matcherSerializer() {
		CodeSerializer codeSerializer = new CodeSerializer("net.amygdalum.testrecorder.scenarios", config -> new ConfigurableSerializerFacade(config), config -> new MatcherGenerators(config));
		codeSerializer.getTypes().registerTypes(SubBean.class);
		return codeSerializer;
	}

}