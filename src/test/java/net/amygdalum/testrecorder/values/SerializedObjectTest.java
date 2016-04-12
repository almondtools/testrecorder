package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedObjectTest {

	@Test
	public void testGetResultType() throws Exception {
		assertThat(new SerializedObject(String.class).getResultType(), equalTo(String.class));
	}

	@Test
	public void testSetGetObjectType() throws Exception {
		SerializedObject serializedObject = new SerializedObject(String.class).withResult(Object.class);

		assertThat(serializedObject.getType(), equalTo(String.class));
	}

	@Test
	public void testWithFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class).withFields(
			new SerializedField(Object.class, "f1", Object.class, literal("str")),
			new SerializedField(Object.class, "f2", Integer.class, literal(2)));

		assertThat(serializedObject.getFields(), contains(
			new SerializedField(Object.class, "f1", Object.class, literal("str")),
			new SerializedField(Object.class, "f2", Integer.class, literal(2))));
	}

	@Test
	public void testGetAddFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		serializedObject.addField(new SerializedField(Object.class, "f1", Object.class, literal("str")));
		serializedObject.addField(new SerializedField(Object.class, "f2", Integer.class, literal(2)));

		assertThat(serializedObject.getFields(), contains(
			new SerializedField(Object.class, "f1", Object.class, literal("str")),
			new SerializedField(Object.class, "f2", Integer.class, literal(2))));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		assertThat(serializedObject.accept(new TestValueVisitor()), equalTo("SerializedObject"));
	}

	@Test
	public void testToString() throws Exception {
		SerializedObject serializedObject = new SerializedObject(String.class).withResult(Object.class);

		serializedObject.addField(new SerializedField(Object.class, "f1", Object.class, literal("str")));
		serializedObject.addField(new SerializedField(Object.class, "f2", Integer.class, literal(2)));

		assertThat(serializedObject.toString(), equalTo("java.lang.String/" + System.identityHashCode(serializedObject) + " {\njava.lang.Object f1: str,\njava.lang.Integer f2: 2\n}"));
	}

}
