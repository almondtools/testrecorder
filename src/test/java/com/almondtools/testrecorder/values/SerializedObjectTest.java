package com.almondtools.testrecorder.values;

import static com.almondtools.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.testrecorder.visitors.TestValueVisitor;

public class SerializedObjectTest {

	@Test
	public void testGetType() throws Exception {
		assertThat(new SerializedObject(String.class).getType(), equalTo(String.class));
	}

	@Test
	public void testSetGetObjectType() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		serializedObject.setObjectType(String.class);

		assertThat(serializedObject.getValueType(), equalTo(String.class));
	}

	@Test
	public void testGetAddFields() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		serializedObject.addField(new SerializedField("f1", Object.class, literal(String.class, "str")));
		serializedObject.addField(new SerializedField("f2", Integer.class, literal(Integer.class, 2)));

		assertThat(serializedObject.getFields(), contains(new SerializedField("f1", Object.class, literal(String.class, "str")), new SerializedField("f2", Integer.class, literal(Integer.class, 2))));

	}

	@Test
	public void testAccept() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		assertThat(serializedObject.accept(new TestValueVisitor()), equalTo("object"));
	}

	@Test
	public void testToString() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);
		serializedObject.setObjectType(String.class);

		serializedObject.addField(new SerializedField("f1", Object.class, literal(String.class, "str")));
		serializedObject.addField(new SerializedField("f2", Integer.class, literal(Integer.class, 2)));

		assertThat(serializedObject.toString(), equalTo("java.lang.String/" + System.identityHashCode(serializedObject) + " {\njava.lang.Object f1: str,\njava.lang.Integer f2: 2\n}"));
	}

}
