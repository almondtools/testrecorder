package net.amygdalum.testrecorder.values;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.GenericTypes.hashMapOfStringListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.hashMapOfStringString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringString;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;
import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedMapTest {

	@Test
	public void testGetResultTypeRaw() throws Exception {
		assertThat(new SerializedMap(HashMap.class).withResult(Map.class).getResultType()).isEqualTo(Map.class);
	}

	@Test
	public void testGetResultTypeParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).withResult(mapOfStringString()).getResultType()).isParameterizedType(Map.class, null, String.class, String.class);
	}

	@Test
	public void testGetResultTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).getResultType()).isParameterizedType(HashMap.class, null, String.class, String.class);
	}

	@Test
	public void testGetResultTypeBounded() throws Exception {
		assertThat(new SerializedMap(HashMap.class).withResult(mapOfBounded()).getResultType()).isInstanceOf(TypeVariable.class);
	}

	@Test
	public void testWithSerializedValueArray() throws Exception {
		SerializedMap result = new SerializedMap(HashMap.class)
			.withResult(hashMapOfStringString())
			.with(Collections.singletonMap(literal("a"), literal("b")));

		assertThat(result).containsExactly(entry(literal("a"), literal("b")));
	}

	@Test
	public void testGetKeyValueTypeRaw() throws Exception {
		assertThat(new SerializedMap(HashMap.class).withResult(Map.class).getMapKeyType()).isEqualTo(Object.class);
		assertThat(new SerializedMap(HashMap.class).withResult(Map.class).getMapValueType()).isEqualTo(Object.class);
	}

	@Test
	public void testGetKeyValueTypeParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).withResult(mapOfStringString()).getMapKeyType()).isEqualTo(String.class);
		assertThat(new SerializedMap(hashMapOfStringString()).withResult(mapOfStringString()).getMapValueType()).isEqualTo(String.class);
	}

	@Test
	public void testGetKeyValueTypeNestedParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringListOfString()).withResult(mapOfStringListOfString()).getMapKeyType()).isEqualTo(String.class);
		assertThat(new SerializedMap(hashMapOfStringListOfString()).withResult(mapOfStringListOfString()).getMapValueType()).isParameterizedType(List.class, null, String.class);
	}

	@Test
	public void testGetKeyValueTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).getMapKeyType()).isEqualTo(String.class);
		assertThat(new SerializedMap(hashMapOfStringString()).getMapValueType()).isEqualTo(String.class);
	}

	@Test
	public void testGetKeyValueTypeBounded() throws Exception {
		assertThat(new SerializedMap(HashMap.class).withResult(mapOfBounded()).getMapKeyType()).isEqualTo(Object.class);
		assertThat(new SerializedMap(HashMap.class).withResult(mapOfBounded()).getMapValueType()).isEqualTo(Object.class);
	}

	@Test
	public void testSize0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);

		assertThat(map.size()).isEqualTo(0);
		assertThat(map.referencedValues()).isEmpty();
	}

	@Test
	public void testSize1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key"), literal("value"));

		assertThat(map.size()).isEqualTo(1);
		assertThat(map.referencedValues()).hasSize(2);
	}

	@Test
	public void testSize2() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key1"), literal("value1"));
		map.put(literal("key2"), literal("value2"));

		assertThat(map.size()).isEqualTo(2);
		assertThat(map.referencedValues()).hasSize(4);
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.isEmpty()).isTrue();
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key"), literal("value"));
		assertThat(map.isEmpty()).isFalse();
	}

	@Test
	public void testContains0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.containsKey(literal("key"))).isFalse();
		assertThat(map.containsValue(literal("value"))).isFalse();
	}

	@Test
	public void testContainsKey1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key"), literal("value"));
		assertThat(map.containsKey(literal("key"))).isTrue();
		assertThat(map.containsKey(literal("string"))).isFalse();
	}

	@Test
	public void testContainsValue1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key"), literal("value"));
		assertThat(map.containsValue(literal("value"))).isTrue();
		assertThat(map.containsValue(literal("string"))).isFalse();
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.remove(literal("string"))).isNull();
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key"), literal("value"));
		assertThat(map.remove(literal("string"))).isNull();
		assertThat(map.remove(literal("key"))).isEqualTo(literal("value"));
	}

	@Test
	public void testPutAll() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		Map<SerializedValue, SerializedValue> value = new HashMap<>();
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		map.putAll(value);

		assertThat(map).contains(entry(literal("key1"), literal("value1")));
		assertThat(map).contains(entry(literal("key2"), literal("value2")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key1"), literal("value1"));
		map.put(literal("key2"), literal("value2"));

		map.clear();

		assertThat(map.keySet()).isEmpty();
	}

	@Test
	public void testKeySet() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key1"), literal("value1"));
		map.put(literal("key2"), literal("value2"));

		assertThat(map.keySet()).containsExactlyInAnyOrder(literal("key1"), literal("key2"));
	}

	@Test
	public void testValues() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key1"), literal("value1"));
		map.put(literal("key2"), literal("value2"));

		assertThat(map.values()).containsExactlyInAnyOrder(literal("value1"), literal("value2"));
	}

	@Test
	public void testGet() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key"), literal("value"));

		assertThat(map.get(literal("key"))).isEqualTo(literal("value"));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);

		assertThat(map.toString()).isEqualTo("{}");
	}

	@Test
	public void testToString1() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		map.put(literal("key"), literal("value"));

		assertThat(map.toString()).isEqualTo("{key:value}");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedMap map = new SerializedMap(HashMap.class).withResult(Map.class);
		assertThat(map.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedMap");
	}

}
