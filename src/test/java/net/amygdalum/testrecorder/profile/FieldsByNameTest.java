package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;
import net.amygdalum.testrecorder.util.testobjects.SimpleNoDefaultConstructor;

public class FieldsByNameTest {

	@Test
	public void testFactoryMethod() throws Exception {
		FieldsByName fieldByName = new FieldsByName("str");
		assertThat(fieldByName).isEqualToComparingFieldByField(Fields.byName("str"));
	}

	@Test
	public void testFactoryMethodRejection() throws Exception {
		assertThatCode(() -> new FieldsByName("String str")).isInstanceOf(IllegalArgumentException.class);
		assertThatCode(() -> new FieldsByName("str;")).isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testMatchesReflectiveField() throws Exception {
		FieldsByName fieldByName = new FieldsByName("str");
		assertThat(fieldByName.matches(Simple.class.getDeclaredField("str"))).isTrue();
		assertThat(fieldByName.matches(SimpleMisleadingFieldName.class.getDeclaredField("str"))).isTrue();
		assertThat(fieldByName.matches(SimpleNoDefaultConstructor.class.getDeclaredField("str"))).isTrue();
		assertThat(fieldByName.matches(Complex.class.getDeclaredField("simple"))).isFalse();
	}

	@Test
	public void testMatchesReflectiveFieldByQualifiedName() throws Exception {
		FieldsByName fieldByName = new FieldsByName("net.amygdalum.testrecorder.util.testobjects.Simple.str");
		assertThat(fieldByName.matches(Simple.class.getDeclaredField("str"))).isTrue();
		assertThat(fieldByName.matches(SimpleMisleadingFieldName.class.getDeclaredField("str"))).isFalse();
		assertThat(fieldByName.matches(SimpleNoDefaultConstructor.class.getDeclaredField("str"))).isFalse();
		assertThat(fieldByName.matches(Complex.class.getDeclaredField("simple"))).isFalse();
	}

	@Test
	public void testMatchesFieldDescriptor() throws Exception {
		FieldsByName fieldByName = new FieldsByName("str");
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;")).isTrue();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "str", "I")).isTrue();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleNoDefaultConstructor", "str", "Ljava/lang/String;")).isTrue();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "simple", "Lnet/amygdalum/testrecorder/util/testobjects/Simple;")).isFalse();
	}

	@Test
	public void testMatchesFieldDescriptorByQualifiedName() throws Exception {
		FieldsByName fieldByName = new FieldsByName("net.amygdalum.testrecorder.util.testobjects.Simple.str");
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;")).isTrue();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "str", "I")).isFalse();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/SimpleNoDefaultConstructor", "str", "Ljava/lang/String;")).isFalse();
		assertThat(fieldByName.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "simple", "Lnet/amygdalum/testrecorder/util/testobjects/Simple;")).isFalse();
	}

}
