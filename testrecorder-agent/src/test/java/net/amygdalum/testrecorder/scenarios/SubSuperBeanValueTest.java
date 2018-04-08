package net.amygdalum.testrecorder.scenarios;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.codeserializer.CodeSerializer;

public class SubSuperBeanValueTest {

	@Test
	public void testCodeSerializerSimple() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createSimpleBean()))
			.containsSequence(
				"subBean1 = new SubBean()",
				"subBean1.setI(53)")
			.doesNotContain("subBean1.setO");
	}

	@Test
	public void testCodeSerializerString() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createStringBean())).contains(
			"subBean1 = new SubBean()",
			"subBean1.setI(53)",
			"subBean1.setO(\"84\")");
	}

	@Test
	public void testCodeSerializerNested() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createNestedBean())).contains(
			"subBean1 = new SubBean()",
			"subBean1.setI(53)",
			"subBean1.setO(subBean2)");
	}

	@Test
	public void testCodeSerializerRecursive() throws Exception {
		CodeSerializer codeSerializer = new CodeSerializer();

		assertThat(codeSerializer.serialize(createRecursiveBean())).contains(
			"subBean1 = new SubBean()",
			"subBean1.setI(53)",
			"subBean1.setO(subBean1)");
	}

	private static SubBean createSimpleBean() {
		SubBean bean = new SubBean();
		bean.setI(53);
		bean.setO(null);
		return bean;
	}

	private static SubBean createStringBean() {
		SubBean bean = new SubBean();
		bean.setI(53);
		bean.setO("84");
		return bean;
	}

	private static SubBean createNestedBean() {
		SubBean bean = new SubBean();
		bean.setI(53);
		bean.setO(new SubBean());
		return bean;
	}

	private static SubBean createRecursiveBean() {
		SubBean bean = new SubBean();
		bean.setI(53);
		bean.setO(bean);
		return bean;
	}

}