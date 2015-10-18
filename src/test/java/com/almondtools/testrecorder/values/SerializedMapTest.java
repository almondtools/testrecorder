package com.almondtools.testrecorder.values;

import static com.almondtools.testrecorder.values.GenericTypes.hashMapOfStringString;
import static com.almondtools.testrecorder.values.GenericTypes.mapOfBounded;
import static com.almondtools.testrecorder.values.GenericTypes.mapOfStringListOfString;
import static com.almondtools.testrecorder.values.GenericTypes.mapOfStringString;
import static com.almondtools.testrecorder.values.ParameterizedTypeMatcher.parameterized;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.almondtools.testrecorder.SerializedValue;

public class SerializedMapTest {

	@Test
	public void testGetTypeRaw() throws Exception {
		assertThat(new SerializedMap(Map.class).getType(), equalTo(Map.class));
	}

	@Test
	public void testGetTypeParameterized() throws Exception {
		assertThat(new SerializedMap(mapOfStringString()).getType(), parameterized(Map.class, String.class, String.class));
	}

	@Test
	public void testGetTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).getType(), parameterized(HashMap.class, String.class, String.class));
	}

	@Test
	public void testGetTypeBounded() throws Exception {
		assertThat(new SerializedMap(mapOfBounded()).getType(), instanceOf(TypeVariable.class));
	}

	@Test
	public void testGetKeyValueTypeRaw() throws Exception {
		assertThat(new SerializedMap(Map.class).getKeyType(), equalTo(Object.class));
		assertThat(new SerializedMap(Map.class).getValueType(), equalTo(Object.class));
	}

	@Test
	public void testGetKeyValueTypeParameterized() throws Exception {
		assertThat(new SerializedMap(mapOfStringString()).getKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(mapOfStringString()).getValueType(), equalTo(String.class));
	}

	@Test
	public void testGetKeyValueTypeNestedParameterized() throws Exception {
		assertThat(new SerializedMap(mapOfStringListOfString()).getKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(mapOfStringListOfString()).getValueType(), parameterized(List.class, String.class));
	}

	@Test
	public void testGetKeyValueTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).getKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(hashMapOfStringString()).getValueType(), equalTo(String.class));
	}

	@Test
	public void testGetKeyValueTypeBounded() throws Exception {
		assertThat(new SerializedMap(mapOfBounded()).getKeyType(), equalTo(Object.class));
		assertThat(new SerializedMap(mapOfBounded()).getValueType(), equalTo(Object.class));
	}

	@Test
	public void testSize0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testSize1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key"), new SerializedLiteral(String.class, "value"));
		assertThat(map.size(), equalTo(1));
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		assertThat(map.isEmpty(), is(true));
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key"), new SerializedLiteral(String.class, "value"));
		assertThat(map.isEmpty(), is(false));
	}

	@Test
	public void testContains0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		assertThat(map.containsKey(new SerializedLiteral(String.class, "key")), is(false));
		assertThat(map.containsValue(new SerializedLiteral(String.class, "value")), is(false));
	}

	@Test
	public void testContainsKey1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key"), new SerializedLiteral(String.class, "value"));
		assertThat(map.containsKey(new SerializedLiteral(String.class, "key")), is(true));
		assertThat(map.containsKey(new SerializedLiteral(String.class, "string")), is(false));
	}

	@Test
	public void testContainsValue1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key"), new SerializedLiteral(String.class, "value"));
		assertThat(map.containsValue(new SerializedLiteral(String.class, "value")), is(true));
		assertThat(map.containsValue(new SerializedLiteral(String.class, "string")), is(false));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		assertThat(map.remove(new SerializedLiteral(String.class, "string")), nullValue());
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key"), new SerializedLiteral(String.class, "value"));
		assertThat(map.remove(new SerializedLiteral(String.class, "string")), nullValue());
		assertThat(map.remove(new SerializedLiteral(String.class, "key")), equalTo(new SerializedLiteral(String.class, "value")));
	}

	@Test
	public void testPutAll() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		Map<SerializedValue, SerializedValue> value = new HashMap<>();
		value.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		value.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));
		
		map.putAll(value);

		assertThat(map, hasEntry(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1")));
		assertThat(map, hasEntry(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		map.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));

		map.clear();

		assertThat(map.keySet(), empty());
	}

	@Test
	public void testKeySet() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		map.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));

		assertThat(map.keySet(), containsInAnyOrder(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "key2")));
	}

	@Test
	public void testValues() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		map.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));

		assertThat(map.values(), containsInAnyOrder(new SerializedLiteral(String.class, "value1"), new SerializedLiteral(String.class, "value2")));
	}

	@Test
	public void testEquals() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		map.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));

		SerializedMap expected = new SerializedMap(Map.class);
		expected.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		expected.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));

		assertThat(map, equalTo(expected));
	}

	@Test
	public void testHashCode() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		map.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));

		SerializedMap expected = new SerializedMap(Map.class);
		expected.put(new SerializedLiteral(String.class, "key1"), new SerializedLiteral(String.class, "value1"));
		expected.put(new SerializedLiteral(String.class, "key2"), new SerializedLiteral(String.class, "value2"));

		assertThat(map.hashCode(), equalTo(expected.hashCode()));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		
		assertThat(map.toString(), equalTo("{}"));
	}

	@Test
	public void testToString1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		map.put(new SerializedLiteral(String.class, "key"), new SerializedLiteral(String.class, "value"));
		
		assertThat(map.toString(), equalTo("{key:value}"));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedMap map = new SerializedMap(Map.class);
		assertThat(map.accept(new TestValueVisitor()), equalTo("unknown"));
		assertThat(map.accept(new TestCollectionVisitor()), equalTo("map"));
	}

}
