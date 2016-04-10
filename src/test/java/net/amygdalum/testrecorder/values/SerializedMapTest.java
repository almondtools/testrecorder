package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.values.GenericTypes.hashMapOfStringListOfString;
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

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedMapTest {

	@Test
		public void testGetResultTypeRaw() throws Exception {
			assertThat(new SerializedMap(HashMap.class).withResult(Map.class).getResultType(), equalTo(Map.class));
		}

	@Test
		public void testGetResultTypeParameterized() throws Exception {
			assertThat(new SerializedMap(hashMapOfStringString()).withResult(mapOfStringString()).getResultType(), parameterized(Map.class, String.class, String.class));
		}

	@Test
		public void testGetResultTypeIndirectParameterized() throws Exception {
			assertThat(new SerializedMap(hashMapOfStringString()).getResultType(), parameterized(HashMap.class, String.class, String.class));
		}

	@Test
		public void testGetResultTypeBounded() throws Exception {
			assertThat(new SerializedMap(HashMap.class).withResult(mapOfBounded()).getResultType(), instanceOf(TypeVariable.class));
		}

	@Test
	public void testGetKeyValueTypeRaw() throws Exception {
		assertThat(new SerializedMap(HashMap.class).withResult(Map.class).getMapKeyType(), equalTo(Object.class));
		assertThat(new SerializedMap(HashMap.class).withResult(Map.class).getMapValueType(), equalTo(Object.class));
	}

	@Test
	public void testGetKeyValueTypeParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).withResult(mapOfStringString()).getMapKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(hashMapOfStringString()).withResult(mapOfStringString()).getMapValueType(), equalTo(String.class));
	}

	@Test
	public void testGetKeyValueTypeNestedParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringListOfString()).withResult(mapOfStringListOfString()).getMapKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(hashMapOfStringListOfString()).withResult(mapOfStringListOfString()).getMapValueType(), parameterized(List.class, String.class));
	}

	@Test
	public void testGetKeyValueTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).getMapKeyType(), equalTo(String.class));
		assertThat(new SerializedMap(hashMapOfStringString()).getMapValueType(), equalTo(String.class));
	}

	@Test
	public void testGetKeyValueTypeBounded() throws Exception {
		assertThat(new SerializedMap(HashMap.class).withResult(mapOfBounded()).getMapKeyType(), equalTo(Object.class));
		assertThat(new SerializedMap(HashMap.class).withResult(mapOfBounded()).getMapValueType(), equalTo(Object.class));
	}

	@Test
	public void testSize0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testSize1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.size(), equalTo(1));
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.isEmpty(), is(true));
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.isEmpty(), is(false));
	}

	@Test
	public void testContains0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.containsKey(literal(String.class, "key")), is(false));
		assertThat(map.containsValue(literal(String.class, "value")), is(false));
	}

	@Test
	public void testContainsKey1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.containsKey(literal(String.class, "key")), is(true));
		assertThat(map.containsKey(literal(String.class, "string")), is(false));
	}

	@Test
	public void testContainsValue1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.containsValue(literal(String.class, "value")), is(true));
		assertThat(map.containsValue(literal(String.class, "string")), is(false));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.remove(literal(String.class, "string")), nullValue());
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		assertThat(map.remove(literal(String.class, "string")), nullValue());
		assertThat(map.remove(literal(String.class, "key")), equalTo(literal(String.class, "value")));
	}

	@Test
	public void testPutAll() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		Map<SerializedValue, SerializedValue> value = new HashMap<>();
		value.put(literal(String.class, "key1"), literal(String.class, "value1"));
		value.put(literal(String.class, "key2"), literal(String.class, "value2"));
		
		map.putAll(value);

		assertThat(map, hasEntry(literal(String.class, "key1"), literal(String.class, "value1")));
		assertThat(map, hasEntry(literal(String.class, "key2"), literal(String.class, "value2")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key1"), literal(String.class, "value1"));
		map.put(literal(String.class, "key2"), literal(String.class, "value2"));

		map.clear();

		assertThat(map.keySet(), empty());
	}

	@Test
	public void testKeySet() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key1"), literal(String.class, "value1"));
		map.put(literal(String.class, "key2"), literal(String.class, "value2"));

		assertThat(map.keySet(), containsInAnyOrder(literal(String.class, "key1"), literal(String.class, "key2")));
	}

	@Test
	public void testValues() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key1"), literal(String.class, "value1"));
		map.put(literal(String.class, "key2"), literal(String.class, "value2"));

		assertThat(map.values(), containsInAnyOrder(literal(String.class, "value1"), literal(String.class, "value2")));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		
		assertThat(map.toString(), equalTo("{}"));
	}

	@Test
	public void testToString1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal(String.class, "key"), literal(String.class, "value"));
		
		assertThat(map.toString(), equalTo("{key:value}"));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.accept(new TestValueVisitor()), equalTo("SerializedMap"));
	}

}
