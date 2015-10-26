package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;

public class ValueSnapshotTest {

	@Test
	public void testValueSnapshot() throws Exception {
		ValueSnapshot snapshot = new ValueSnapshot(ArrayList.class, Object[].class, "elementData");
		
		assertThat(snapshot.isValid(), is(true));
		assertThat(snapshot.getDeclaringClass(), equalTo(ArrayList.class));
		assertThat(snapshot.getType(), equalTo(Object[].class));
		assertThat(snapshot.getFieldName(), equalTo("elementData"));
	}

	@Test
	public void testInvalidate() throws Exception {
		ValueSnapshot snapshot = new ValueSnapshot(ArrayList.class, Object[].class, "elementData");
		
		snapshot.invalidate();
		
		assertThat(snapshot.isValid(), is(false));
	}

	@Test
	public void testGetValueType() throws Exception {
		ValueSnapshot snapshot = new ValueSnapshot(ArrayList.class, Object[].class, "elementData");
		
		snapshot.setValue(literal(String.class, "value"));
		
		assertThat(snapshot.getValueType(), equalTo(String.class));
	}

	@Test
	public void testGetValueTypeNull() throws Exception {
		ValueSnapshot snapshot = new ValueSnapshot(ArrayList.class, Object[].class, "elementData");
		
		assertThat(snapshot.getValueType(), nullValue());
	}

	@Test
	public void testSetGetValue() throws Exception {
		ValueSnapshot snapshot = new ValueSnapshot(ArrayList.class, Object[].class, "elementData");
		
		snapshot.setValue(literal(String.class, "value"));
		
		assertThat(snapshot.getValue(), equalTo(literal(String.class, "value")));
	}

}
