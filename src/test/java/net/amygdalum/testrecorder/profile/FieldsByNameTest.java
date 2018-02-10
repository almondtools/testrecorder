package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;
import net.amygdalum.testrecorder.util.testobjects.SimpleNoDefaultConstructor;

public class FieldsByNameTest {

	private FieldsByName fieldByName;

	@BeforeEach
	public void before() {
		fieldByName = new FieldsByName("str");
	}

	@Test
	public void testFactoryMethod() throws Exception {
		assertThat(fieldByName).isEqualToComparingFieldByField(Fields.byName("str"));
	}

	@Test
	public void testMatchesReflectiveField() throws Exception {
		assertThat(fieldByName.matches(Simple.class.getDeclaredField("str"))).isTrue();
		assertThat(fieldByName.matches(SimpleMisleadingFieldName.class.getDeclaredField("str"))).isTrue();
		assertThat(fieldByName.matches(SimpleNoDefaultConstructor.class.getDeclaredField("str"))).isTrue();
		assertThat(fieldByName.matches(Complex.class.getDeclaredField("simple"))).isFalse();
	}

	@Test
	public void testMatchesFieldDescriptor() throws Exception {
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;")).isTrue();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "str", "I")).isTrue();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleNoDefaultConstructor", "str", "Ljava/lang/String;")).isTrue();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "simple", "Lnet/amygdalum/testrecorder/util/testobjects/Simple;")).isFalse();
	}

}
