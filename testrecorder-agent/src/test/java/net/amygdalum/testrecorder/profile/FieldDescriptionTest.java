package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.util.testobjects.SimpleMisleadingFieldName;

public class FieldDescriptionTest {

	private FieldDescription fieldByDescription;

	@BeforeEach
	public void before() {
		fieldByDescription = new FieldDescription("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;");
	}

	@Test
	public void testFieldDescription() throws Exception {
		assertThat(fieldByDescription).isEqualToComparingFieldByField(Fields.byDescription("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;"));
		assertThat(fieldByDescription).isEqualToComparingFieldByField(Fields.byDescription(Simple.class.getDeclaredField("str")));
	}

	@Nested
	class testMatches {
		@Test
		public void onReflectiveField() throws Exception {
			assertThat(fieldByDescription.matches(Simple.class.getDeclaredField("str"))).isTrue();
			assertThat(fieldByDescription.matches(SimpleMisleadingFieldName.class.getDeclaredField("str"))).isFalse();
			assertThat(fieldByDescription.matches(Complex.class.getDeclaredField("simple"))).isFalse();
		}

		@Test
		public void onFieldDescriptor() throws Exception {
			assertThat(fieldByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Simple", "str", "Ljava/lang/String;")).isTrue();
			assertThat(fieldByDescription.matches("net/amygdalum/testrecorder/util/testobjects/SimpleMisleadingFieldName", "str", "I")).isFalse();
			assertThat(fieldByDescription.matches("net/amygdalum/testrecorder/util/testobjects/Complex", "simple", "Lnet/amygdalum/testrecorder/util/testobjects/Simple;")).isFalse();
		}
	}
}
