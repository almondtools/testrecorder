package net.amygdalum.testrecorder.scenarios;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;

public class ClassicBeanValueTest {

	@Test
	public void testCodeSerializerSimple() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createSimpleBean()))
			.containsSequence(
				"classicBean1 = new ClassicBean()",
				"classicBean1.setI(22)")
			.doesNotContain("classicBean1.setO");
	}

	@Test
	public void testCodeSerializerString() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createStringBean())).containsSequence(
			"classicBean1 = new ClassicBean()",
			"classicBean1.setI(22)",
			"classicBean1.setO(\"33\")");
	}

	@Test
	public void testCodeSerializerNested() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createNestedBean())).containsSequence(
			"classicBean1 = new ClassicBean()",
			"classicBean1.setI(22)",
			"classicBean1.setO(classicBean2)");
	}

	@Test
	public void testCodeSerializerRecursive() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createRecursiveBean())).containsSequence(
			"classicBean1 = new ClassicBean()",
			"classicBean1.setI(22)",
			"classicBean1.setO(classicBean1)");
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