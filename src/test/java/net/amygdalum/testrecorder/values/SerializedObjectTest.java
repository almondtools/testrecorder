package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.visitors.TestValueVisitor;

import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SerializedObjectTest {

	@Test
	public void testGetType() throws Exception {
		assertThat(new SerializedObject(String.class,String.class).getType(), equalTo(String.class));
	}

	@Test
	public void testSetGetObjectType() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class, String.class);

		assertThat(serializedObject.getValueType(), equalTo(String.class));
	}

	@Test
	public void testGetAddFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class, Object.class);

		serializedObject.addField(new SerializedField(Object.class, "f1", Object.class, literal(String.class, "str")));
		serializedObject.addField(new SerializedField(Object.class, "f2", Integer.class, literal(Integer.class, 2)));

		assertThat(serializedObject.getFields(), contains(
			new SerializedField(Object.class, "f1", Object.class, literal(String.class, "str")), 
			new SerializedField(Object.class, "f2", Integer.class, literal(Integer.class, 2))));

	}

	@Test
	public void testAccept() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class, Object.class);

		assertThat(serializedObject.accept(new TestValueVisitor()), equalTo("object"));
	}

	@Test
	public void testToString() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class, String.class);

		serializedObject.addField(new SerializedField(Object.class, "f1", Object.class, literal(String.class, "str")));
		serializedObject.addField(new SerializedField(Object.class, "f2", Integer.class, literal(Integer.class, 2)));

		assertThat(serializedObject.toString(), equalTo("java.lang.String/" + System.identityHashCode(serializedObject) + " {\njava.lang.Object f1: str,\njava.lang.Integer f2: 2\n}"));
	}

}
