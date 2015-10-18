package com.almondtools.testrecorder.values;

import static com.almondtools.testrecorder.values.SerializedLiteral.literal;
import static com.almondtools.util.objects.EqualityMatcher.satisfiesDefaultEquality;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SerializedObjectTest {

	@Test
	public void testGetType() throws Exception {
		assertThat(new SerializedObject(String.class).getType(), equalTo(String.class));
	}

	@Test
	public void testSetGetObjectType() throws Exception {
		SerializedObject serializedObject = new SerializedObject(Object.class);

		serializedObject.setObjectType(String.class);

		assertThat(serializedObject.getObjectType(), equalTo(String.class));
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

		assertThat(serializedObject.toString(), equalTo("java.lang.Object/" + System.identityHashCode(serializedObject) + " {\njava.lang.Object f1: str,\njava.lang.Integer f2: 2\n}"));
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(new SerializedObject(Object.class), satisfiesDefaultEquality()
			.andEqualTo(new SerializedObject(String.class))
			.andNotEqualTo(new SerializedObject(Object.class).withObjectType(String.class))
			.andNotEqualTo(new SerializedObject(String.class).withFields(new SerializedField("f", Object.class, literal(String.class, "str")))));
		assertThat(new SerializedObject(Object.class).withObjectType(String.class), satisfiesDefaultEquality()
			.andEqualTo(new SerializedObject(Object.class).withObjectType(String.class))
			.andNotEqualTo(new SerializedObject(Object.class))
			.andNotEqualTo(new SerializedObject(String.class)));
	}

}
