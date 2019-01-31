package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.extensions.assertj.conventions.DefaultEquality;

public class FieldSignatureTest {

	@Nested
	class testFieldSignature {
		@Test
		void onDeclaringClassNull() throws Exception {
			assertThatThrownBy(() -> new FieldSignature(null, String.class, "field")).isInstanceOf(AssertionError.class);
		}

		@Test
		void onTypeNull() throws Exception {
			assertThatThrownBy(() -> new FieldSignature(MyObject.class, null, "field")).isInstanceOf(AssertionError.class);
		}

		@Test
		void onFieldNameNull() throws Exception {
			assertThatThrownBy(() -> new FieldSignature(MyObject.class, String.class, null)).isInstanceOf(AssertionError.class);
		}
	}

	@Test
	void testSerializable() throws Exception {
		FieldSignature signature = new FieldSignature(MyObject.class, String.class, "field");

		FieldSignature deserialized = new TestDeSerializer().deSerialize(signature);

		assertThat(deserialized).isEqualTo(signature);
	}

	@Test
	void testResolveField() throws Exception {
		FieldSignature signature = new FieldSignature(MyObject.class, String.class, "field");

		Field field = signature.resolveField();
		assertThat(field.getDeclaringClass()).isSameAs(MyObject.class);
		assertThat(field.getType()).isEqualTo(String.class);
		assertThat(field.getName()).isEqualTo("field");
	}

	@Test
	void testToString() throws Exception {
		FieldSignature signature = new FieldSignature(MyObject.class, String.class, "field");
		assertThat(signature.toString()).isEqualTo("java.lang.String field of net.amygdalum.testrecorder.types.FieldSignatureTest$MyObject");
	}

	@Test
	void testEquals() throws Exception {
		FieldSignature signature = new FieldSignature(MyObject.class, String.class, "field");
		assertThat(signature).satisfies(DefaultEquality.defaultEquality()
			.andEqualTo(new FieldSignature(MyObject.class, String.class, "field"))
			.andNotEqualTo(new FieldSignature(MyObject.class, Object.class, "field"))
			.andNotEqualTo(new FieldSignature(MyObject.class, String.class, "other"))
			.andNotEqualTo(new FieldSignature(Object.class, String.class, "field"))
			.conventions());
	}

	public static class MyObject {
		public String field;
	}

}
