package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedObjectTest {

	@Test
	public void testGetResultType() throws Exception {
		assertThat(new SerializedObject(String.class).getUsedTypes()).containsExactly(String.class);
	}

	@Test
	public void testSetGetObjectType() throws Exception {
		SerializedObject value = new SerializedObject(String.class);
		value.useAs(Object.class);

		assertThat(value.getType()).isEqualTo(String.class);
	}

	@Test
	public void testWithFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class).withFields(
			new SerializedField(Object.class, "f1", Object.class, literal("str")),
			new SerializedField(Object.class, "f2", Integer.class, literal(2)));

		assertThat(serializedObject.getFields()).containsExactly(
			new SerializedField(Object.class, "f1", Object.class, literal("str")),
			new SerializedField(Object.class, "f2", Integer.class, literal(2)));
	}

	@Test
	public void testGetAddFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		serializedObject.addField(new SerializedField(Object.class, "f1", Object.class, literal("str")));
		serializedObject.addField(new SerializedField(Object.class, "f2", Integer.class, literal(2)));

		assertThat(serializedObject.getFields()).containsExactly(
			new SerializedField(Object.class, "f1", Object.class, literal("str")),
			new SerializedField(Object.class, "f2", Integer.class, literal(2)));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		assertThat(serializedObject.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedObject");
	}

	@Test
	public void testToString() throws Exception {
		SerializedObject value = new SerializedObject(String.class);
		value.useAs(Object.class);

		value.addField(new SerializedField(Object.class, "f1", Object.class, literal("str")));
		value.addField(new SerializedField(Object.class, "f2", Integer.class, literal(2)));

		assertThat(value.toString()).isEqualTo("java.lang.String/" + System.identityHashCode(value) + " {\njava.lang.Object f1: str,\njava.lang.Integer f2: 2\n}");
	}

}
