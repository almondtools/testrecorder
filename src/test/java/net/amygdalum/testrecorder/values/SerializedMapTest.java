package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.values.GenericTypes.hashMapOfStringString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringString;
import static net.amygdalum.testrecorder.values.ParameterizedTypeMatcher.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
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

import net.amygdalum.testrecorder.visitors.TestValueVisitor;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedMap;

public class SerializedMapTest {

	@Test
	public void testGetTypeRaw() throws Exception {
		assertThat(new SerializedMap(Map.class, HashMap.class).getType(), equalTo(Map.class));
	}

	@Test
	public void testGetTypeParameterized() throws Exception {
		assertThat(new SerializedMap(mapOfStringString(), HashMap.class).getType(), parameterized(Map.class, String.class, String.class));
	}

	@Test
	public void testGetTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString(), HashMap.class).getType(), parameterized(HashMap.class, String.class, String.class));
	}

	@Test
	public void testGetTypeBounded() throws Exception {
		assertThat(new SerializedMap(mapOfBounded(), HashMap.class).getType(), instanceOf(TypeVariable.class));
	}

	@Test
	public void testGetKeyValueTypeRaw() throws Exception {
		assertThat(new SerializedMap(Map.class, HashMap.class).getMapKeyType(), equalTo(Object.class));
		assertThat(new SerializedMap(Map.class, HashMap.class).getMapValueType(), equalTo(Object.class));
	}

	@Test
	public void testGetKeyValueTypeParameterized() throws Exception {
		assertThat(new SerializedMap(mapOfStringString(), HashMap.class).getMapKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(mapOfStringString(), HashMap.class).getMapValueType(), equalTo(String.class));
	}

	@Test
	public void testGetKeyValueTypeNestedParameterized() throws Exception {
		assertThat(new SerializedMap(mapOfStringListOfString(), HashMap.class).getMapKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(mapOfStringListOfString(), HashMap.class).getMapValueType(), parameterized(List.class, String.class));
	}

	@Test
	public void testGetKeyValueTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString(), HashMap.class).getMapKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(hashMapOfStringString(), HashMap.class).getMapValueType(), equalTo(String.class));
	}

	@Test
	public void testGetKeyValueTypeBounded() throws Exception {
		assertThat(new SerializedMap(mapOfBounded(), HashMap.class).getMapKeyType(), equalTo(Object.class));
		assertThat(new SerializedMap(mapOfBounded(), HashMap.class).getMapValueType(), equalTo(Object.class));
	}

	@Test
	public void testSize0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testSize1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.size(), equalTo(1));
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		assertThat(map.isEmpty(), is(true));
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.isEmpty(), is(false));
	}

	@Test
	public void testContains0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		assertThat(map.containsKey(literal(String.class, "key")), is(false));
		assertThat(map.containsValue(literal(String.class, "value")), is(false));
	}

	@Test
	public void testContainsKey1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.containsKey(literal(String.class, "key")), is(true));
		assertThat(map.containsKey(literal(String.class, "string")), is(false));
	}

	@Test
	public void testContainsValue1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.containsValue(literal(String.class, "value")), is(true));
		assertThat(map.containsValue(literal(String.class, "string")), is(false));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		assertThat(map.remove(literal(String.class, "string")), nullValue());
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.remove(literal(String.class, "string")), nullValue());
		assertThat(map.remove(literal(String.class, "key")), equalTo(literal(String.class, "value")));
	}

	@Test
	public void testPutAll() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		Map<SerializedValue, SerializedValue> value = new HashMap<>();
		value.put(literal(String.class, "key1"), literal(String.class, "value1"));
		value.put(literal(String.class, "key2"), literal(String.class, "value2"));
		
		map.putAll(value);

		assertThat(map, hasEntry(literal(String.class, "key1"), literal(String.class, "value1")));
		assertThat(map, hasEntry(literal(String.class, "key2"), literal(String.class, "value2")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key1"), literal(String.class, "value1"));
		map.put(literal(String.class, "key2"), literal(String.class, "value2"));

		map.clear();

		assertThat(map.keySet(), empty());
	}

	@Test
	public void testKeySet() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key1"), literal(String.class, "value1"));
		map.put(literal(String.class, "key2"), literal(String.class, "value2"));

		assertThat(map.keySet(), containsInAnyOrder(literal(String.class, "key1"), literal(String.class, "key2")));
	}

	@Test
	public void testValues() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key1"), literal(String.class, "value1"));
		map.put(literal(String.class, "key2"), literal(String.class, "value2"));

		assertThat(map.values(), containsInAnyOrder(literal(String.class, "value1"), literal(String.class, "value2")));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		
		assertThat(map.toString(), equalTo("{}"));
	}

	@Test
	public void testToString1() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		
		assertThat(map.toString(), equalTo("{key:value}"));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedMap map = new SerializedMap(Map.class, HashMap.class);
		assertThat(map.accept(new TestValueVisitor()), equalTo("unknown"));
		assertThat(map.accept(new TestCollectionVisitor()), equalTo("map"));
	}

}
