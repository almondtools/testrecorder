package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class SerializedFieldTest {

	@Test
	void testGetName() throws Exception {
		FieldSignature signature = new FieldSignature(Object.class, String.class, "field");
		assertThat(new SerializedField(signature, literal("stringvalue")).getName()).isEqualTo("field");
	}

	@Test
	void testGetType() throws Exception {
		FieldSignature signature = new FieldSignature(Object.class, String.class, "field");
		assertThat(new SerializedField(signature, literal("stringvalue")).getType()).isEqualTo(String.class);
	}

	@Test
	void testGetValue() throws Exception {
		FieldSignature signature = new FieldSignature(Object.class, String.class, "field");
		assertThat(new SerializedField(signature, literal("stringvalue")).getValue()).isEqualTo(literal("stringvalue"));
	}

	@Test
	void testAccept() throws Exception {
		FieldSignature signature = new FieldSignature(Object.class, String.class, "f");
		assertThat(new SerializedField(signature, literal("sv"))
			.accept(new TestValueVisitor())).isEqualTo("field");
	}

	@Test
	void testToString() throws Exception {
		FieldSignature signature = new FieldSignature(Object.class, String.class, "f");
		assertThat(new SerializedField(signature, literal("sv")).toString()).isEqualTo("java.lang.String f: sv");
	}

	@Test
	void testGetDeclaringClass() throws Exception {
		FieldSignature signature = new FieldSignature(Object.class, String.class, "f");
		assertThat(new SerializedField(signature, literal("sv")).getDeclaringClass()).isEqualTo(Object.class);
	}

	@Test
	void testEquals() throws Exception {
		FieldSignature signature = new FieldSignature(Object.class, String.class, "f");
		FieldSignature othersignature = new FieldSignature(Object.class, String.class, "nf");
		assertThat(new SerializedField(signature, literal("sv"))).satisfies(defaultEquality()
			.andEqualTo(new SerializedField(signature, literal("sv")))
			.andNotEqualTo(new SerializedField(othersignature, literal("sv")))
			.andNotEqualTo(new SerializedField(signature, literal("nsv")))
			.conventions());
	}

	@Test
	void testCompareTo() throws Exception {
		assertThat(Stream.of(
			new SerializedField(new FieldSignature(Object.class, String.class, "a"), literal("stringvalue")),
			new SerializedField(new FieldSignature(Object.class, String.class, "b"), literal("stringvalue"))).sorted())
				.containsExactly(
					new SerializedField(new FieldSignature(Object.class, String.class, "a"), literal("stringvalue")),
					new SerializedField(new FieldSignature(Object.class, String.class, "b"), literal("stringvalue")));
		assertThat(Stream.of(
			new SerializedField(new FieldSignature(Object.class, String.class, "b"), literal("stringvalue")),
			new SerializedField(new FieldSignature(Object.class, String.class, "a"), literal("stringvalue"))).sorted())
				.containsExactly(
					new SerializedField(new FieldSignature(Object.class, String.class, "a"), literal("stringvalue")),
					new SerializedField(new FieldSignature(Object.class, String.class, "b"), literal("stringvalue")));
	}

}
