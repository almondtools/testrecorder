package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedObjectTest {

	@Test
	void testGetResultType() throws Exception {
		assertThat(new SerializedObject(String.class).getUsedTypes()).containsExactly(String.class);
	}

	@Test
	void testSetGetObjectType() throws Exception {
		SerializedObject value = new SerializedObject(String.class);
		value.useAs(Object.class);

		assertThat(value.getType()).isEqualTo(String.class);
	}

	@Test
	void testWithFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class).withFields(
			new SerializedField(new FieldSignature(Object.class, Object.class, "f1"), literal("str")),
			new SerializedField(new FieldSignature(Object.class, Integer.class, "f2"), literal(2)));

		assertThat(serializedObject.getFields()).containsExactly(
			new SerializedField(new FieldSignature(Object.class, Object.class, "f1"), literal("str")),
			new SerializedField(new FieldSignature(Object.class, Integer.class, "f2"), literal(2)));
	}

	@Test
	void testGetAddFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		FieldSignature f1 = new FieldSignature(Object.class, Object.class, "f1");
		FieldSignature f2 = new FieldSignature(Object.class, Integer.class, "f2");
		serializedObject.addField(new SerializedField(f1, literal("str")));
		serializedObject.addField(new SerializedField(f2, literal(2)));

		assertThat(serializedObject.getFields()).containsExactly(
			new SerializedField(f1, literal("str")),
			new SerializedField(f2, literal(2)));
		assertThat(serializedObject.getField("f1")).map(field -> field.getValue()).contains(literal("str"));
		assertThat(serializedObject.getField("f2")).map(field -> field.getValue()).contains(literal(2));
		assertThat(serializedObject.getField("f3")).isNotPresent();
	}

	@Test
	void testAccept() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		assertThat(serializedObject.accept(new TestValueVisitor())).isEqualTo("ReferenceType:SerializedObject");
	}

	@Test
	void testToString() throws Exception {
		SerializedObject value = new SerializedObject(String.class);
		value.useAs(Object.class);

		FieldSignature f1 = new FieldSignature(Object.class, Object.class, "f1");
		value.addField(new SerializedField(f1, literal("str")));
		FieldSignature f2 = new FieldSignature(Object.class, Integer.class, "f2");
		value.addField(new SerializedField(f2, literal(2)));

		assertThat(value.toString()).isEqualTo("java.lang.String/" + System.identityHashCode(value) + " {\njava.lang.Object f1: str,\njava.lang.Integer f2: 2\n}");
	}

}
