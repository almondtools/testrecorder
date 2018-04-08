package net.amygdalum.testrecorder.values;

import static java.util.Comparator.comparing;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.GenericTypes.hashMapOfStringListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.hashMapOfStringString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringString;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedMapTest {

	@Test
	public void testGetResultTypeRaw() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		assertThat(value.getUsedTypes()).containsExactly(Map.class);
	}

	@Test
	public void testGetResultTypeParameterized() throws Exception {
		SerializedMap value = new SerializedMap(hashMapOfStringString());
		value.useAs(mapOfStringString());
		assertThat(value.getUsedTypes())
			.usingElementComparator(comparing(Type::getTypeName))
			.containsExactly(parameterized(Map.class, null, String.class, String.class));
	}

	@Test
	public void testGetResultTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).getUsedTypes())
			.usingElementComparator(comparing(Type::getTypeName))
			.containsExactly(parameterized(HashMap.class, null, String.class, String.class));
	}

	@Test
	public void testGetResultTypeBounded() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(mapOfBounded());
		assertThat(value.getUsedTypes()).allMatch(type -> type instanceof TypeVariable);
	}

	@Test
	public void testWithSerializedValueArray() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class)
			.with(Collections.singletonMap(literal("a"), literal("b")));
		value.useAs(hashMapOfStringString());

		assertThat(value).containsExactly(entry(literal("a"), literal("b")));
	}

	@Test
	public void testGetKeyValueTypeRaw() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		assertThat(value.getMapKeyType()).isEqualTo(Object.class);
		assertThat(value.getMapValueType()).isEqualTo(Object.class);
	}

	@Test
	public void testGetKeyValueTypeParameterized() throws Exception {
		SerializedMap value = new SerializedMap(hashMapOfStringString());
		value.useAs(mapOfStringString());
		assertThat(value.getMapKeyType()).isEqualTo(String.class);
		assertThat(value.getMapValueType()).isEqualTo(String.class);
	}

	@Test
	public void testGetKeyValueTypeNestedParameterized() throws Exception {
		SerializedMap value = new SerializedMap(hashMapOfStringListOfString());
		value.useAs(mapOfStringListOfString());
		assertThat(value.getMapKeyType()).isEqualTo(String.class);
		assertThat(value.getMapValueType()).isEqualTo(parameterized(List.class, null, String.class));
	}

	@Test
	public void testGetKeyValueTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedMap(hashMapOfStringString()).getMapKeyType()).isEqualTo(String.class);
		assertThat(new SerializedMap(hashMapOfStringString()).getMapValueType()).isEqualTo(String.class);
	}

	@Test
	public void testGetKeyValueTypeBounded() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(mapOfBounded());
		assertThat(value.getMapKeyType()).isEqualTo(Object.class);
		assertThat(value.getMapValueType()).isEqualTo(Object.class);
	}

	@Test
	public void testSize0() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);

		assertThat(value.size()).isEqualTo(0);
		assertThat(value.referencedValues()).isEmpty();
	}

	@Test
	public void testSize1() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));

		assertThat(value.size()).isEqualTo(1);
		assertThat(value.referencedValues()).hasSize(2);
	}

	@Test
	public void testSize2() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		assertThat(value.size()).isEqualTo(2);
		assertThat(value.referencedValues()).hasSize(4);
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		assertThat(value.isEmpty()).isTrue();
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));
		assertThat(value.isEmpty()).isFalse();
	}

	@Test
	public void testContains0() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		assertThat(value.containsKey(literal("key"))).isFalse();
		assertThat(value.containsValue(literal("value"))).isFalse();
	}

	@Test
	public void testContainsKey1() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));
		assertThat(value.containsKey(literal("key"))).isTrue();
		assertThat(value.containsKey(literal("string"))).isFalse();
	}

	@Test
	public void testContainsValue1() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));
		assertThat(value.containsValue(literal("value"))).isTrue();
		assertThat(value.containsValue(literal("string"))).isFalse();
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		assertThat(value.remove(literal("string"))).isNull();
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));
		assertThat(value.remove(literal("string"))).isNull();
		assertThat(value.remove(literal("key"))).isEqualTo(literal("value"));
	}

	@Test
	public void testPutAll() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		Map<SerializedValue, SerializedValue> entries = new HashMap<>();
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		value.putAll(entries);

		assertThat(value).contains(entry(literal("key1"), literal("value1")));
		assertThat(value).contains(entry(literal("key2"), literal("value2")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		value.clear();

		assertThat(value.keySet()).isEmpty();
	}

	@Test
	public void testKeySet() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		assertThat(value.keySet()).containsExactlyInAnyOrder(literal("key1"), literal("key2"));
	}

	@Test
	public void testValues() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		assertThat(value.values()).containsExactlyInAnyOrder(literal("value1"), literal("value2"));
	}

	@Test
	public void testGet() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));

		assertThat(value.get(literal("key"))).isEqualTo(literal("value"));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);

		assertThat(value.toString()).isEqualTo("{}");
	}

	@Test
	public void testToString1() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));

		assertThat(value.toString()).isEqualTo("{key:value}");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedMap");
	}

}
