package net.amygdalum.testrecorder.values;

import static java.util.Comparator.comparing;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.GenericTypes.hashMapOfStringString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.mapOfStringString;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedMapTest {

	@Nested
	class testGetResultType {
		@Test
		void onRaw() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.getUsedTypes()).containsExactly(Map.class);
		}

		@Test
		void onParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfStringString());

			assertThat(value.getUsedTypes())
				.usingElementComparator(comparing(Type::getTypeName))
				.containsExactly(parameterized(Map.class, null, String.class, String.class));
		}

		@Test
		void onIndirectParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(hashMapOfStringString());

			assertThat(value.getUsedTypes())
				.usingElementComparator(comparing(Type::getTypeName))
				.containsExactly(parameterized(HashMap.class, null, String.class, String.class));
		}

		@Test
		void onBounded() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfBounded());

			assertThat(value.getUsedTypes()).allMatch(type -> type instanceof TypeVariable);
		}
	}

	@Test
	void testWithSerializedValueArray() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.put(literal("a"), literal("b"));
		value.useAs(hashMapOfStringString());

		assertThat(value).containsExactly(entry(literal("a"), literal("b")));
	}

	@Nested
	class testGetMapKeyType {
		@Test
		void onRaw() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.getMapKeyType()).isEqualTo(Object.class);
		}

		@Test
		void onParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfStringString());

			assertThat(value.getMapKeyType()).isEqualTo(String.class);
		}

		@Test
		void onNestedParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfStringListOfString());

			assertThat(value.getMapKeyType()).isEqualTo(String.class);
		}

		@Test
		void onIndirectParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(hashMapOfStringString());

			assertThat(value.getMapKeyType()).isEqualTo(String.class);
		}

		@Test
		void onBounded() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfBounded());

			assertThat(value.getMapKeyType()).isEqualTo(Object.class);
		}
	}

	@Nested
	class testGetMapValueType {
		@Test
		void onRaw() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.getMapValueType()).isEqualTo(Object.class);
		}

		@Test
		void onParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfStringString());

			assertThat(value.getMapValueType()).isEqualTo(String.class);
		}

		@Test
		void onNestedParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfStringListOfString());

			assertThat(value.getMapValueType()).isEqualTo(parameterized(List.class, null, String.class));
		}

		@Test
		void onIndirectParameterized() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(hashMapOfStringString());

			assertThat(value.getMapValueType()).isEqualTo(String.class);
		}

		@Test
		void onBounded() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(mapOfBounded());

			assertThat(value.getMapValueType()).isEqualTo(Object.class);
		}
	}

	@Nested
	class testSize {
		@Test
		void onSize0() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.size()).isEqualTo(0);
			assertThat(value.referencedValues()).isEmpty();
		}

		@Test
		void onSize1() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);
			value.put(literal("key"), literal("value"));

			assertThat(value.size()).isEqualTo(1);
			assertThat(value.referencedValues()).hasSize(2);
		}

		@Test
		void onSize2() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);
			value.put(literal("key1"), literal("value1"));
			value.put(literal("key2"), literal("value2"));

			assertThat(value.size()).isEqualTo(2);
			assertThat(value.referencedValues()).hasSize(4);
		}
	}

	@Nested
	class testIsEmpty {
		@Test
		void onSize0() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.isEmpty()).isTrue();
		}

		@Test
		void onSize1() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);
			value.put(literal("key"), literal("value"));

			assertThat(value.isEmpty()).isFalse();
		}
	}

	@Nested
	class testContainsKey {

		@Test
		void onSize0() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.containsKey(literal("key"))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);
			value.put(literal("key"), literal("value"));

			assertThat(value.containsKey(literal("key"))).isTrue();
			assertThat(value.containsKey(literal("string"))).isFalse();
		}
	}

	@Nested
	class testContainsValue {

		@Test
		void onSize0() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.containsValue(literal("value"))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);
			value.put(literal("key"), literal("value"));

			assertThat(value.containsValue(literal("value"))).isTrue();
			assertThat(value.containsValue(literal("string"))).isFalse();
		}
	}

	@Nested
	class testRemove {
		@Test
		void onSize0() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.remove(literal("string"))).isNull();
		}

		@Test
		void onSize1() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);
			value.put(literal("key"), literal("value"));

			assertThat(value.remove(literal("string"))).isNull();
			assertThat(value.remove(literal("key"))).isEqualTo(literal("value"));
		}
	}

	@Nested
	class testPut {
		@Test
		void withAssignableType() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(parameterized(Map.class, null, String.class, String.class));

			value.put(literal("key"), literal("value"));

			assertThat(value).contains(entry(literal("key"), literal("value")));
			assertThat(value.getMapKeyType()).isEqualTo(String.class);
			assertThat(value.getMapValueType()).isEqualTo(String.class);
		}

		@Test
		void withResettingKeyType() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(parameterized(Map.class, null, String.class, String.class));

			value.put(literal(1), literal("value"));

			assertThat(value).contains(entry(literal(1), literal("value")));
			assertThat(value.getMapKeyType()).isEqualTo(Object.class);
			assertThat(value.getMapValueType()).isEqualTo(String.class);
		}

		@Test
		void withResettingValueType() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(parameterized(Map.class, null, String.class, String.class));

			value.put(literal("key"), literal(2));

			assertThat(value).contains(entry(literal("key"), literal(2)));
			assertThat(value.getMapKeyType()).isEqualTo(String.class);
			assertThat(value.getMapValueType()).isEqualTo(Object.class);
		}
	}

	@Nested
	class testPutAll {
		@Test
		void withAssignableTypes() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(parameterized(Map.class, null, String.class, String.class));
			Map<SerializedValue, SerializedValue> entries = new HashMap<>();
			entries.put(literal("key1"), literal("value1"));
			entries.put(literal("key2"), literal("value2"));

			value.putAll(entries);

			assertThat(value).contains(entry(literal("key1"), literal("value1")));
			assertThat(value).contains(entry(literal("key2"), literal("value2")));
			assertThat(value.getMapKeyType()).isEqualTo(String.class);
			assertThat(value.getMapValueType()).isEqualTo(String.class);
		}

		@Test
		void withResettingKeyType() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(parameterized(Map.class, null, String.class, String.class));
			Map<SerializedValue, SerializedValue> entries = new HashMap<>();
			entries.put(literal(1), literal("value1"));
			entries.put(literal("key2"), literal("value2"));

			value.putAll(entries);

			assertThat(value).contains(entry(literal(1), literal("value1")));
			assertThat(value).contains(entry(literal("key2"), literal("value2")));
			assertThat(value.getMapKeyType()).isEqualTo(Object.class);
			assertThat(value.getMapValueType()).isEqualTo(String.class);
		}

		@Test
		void withResettingValueType() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(parameterized(Map.class, null, String.class, String.class));
			Map<SerializedValue, SerializedValue> entries = new HashMap<>();
			entries.put(literal("key1"), literal("value1"));
			entries.put(literal("key2"), literal(2));

			value.putAll(entries);

			assertThat(value).contains(entry(literal("key1"), literal("value1")));
			assertThat(value).contains(entry(literal("key2"), literal(2)));
			assertThat(value.getMapKeyType()).isEqualTo(String.class);
			assertThat(value.getMapValueType()).isEqualTo(Object.class);
		}
	}

	@Test
	void testClear() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		value.clear();

		assertThat(value.keySet()).isEmpty();
	}

	@Test
	void testKeySet() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		assertThat(value.keySet()).containsExactlyInAnyOrder(literal("key1"), literal("key2"));
	}

	@Test
	void testValues() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key1"), literal("value1"));
		value.put(literal("key2"), literal("value2"));

		assertThat(value.values()).containsExactlyInAnyOrder(literal("value1"), literal("value2"));
	}

	@Test
	void testGet() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);
		value.put(literal("key"), literal("value"));

		assertThat(value.get(literal("key"))).isEqualTo(literal("value"));
	}

	@Nested
	class toString {
		@Test
		void onSize0() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);

			assertThat(value.toString()).isEqualTo("{}");
		}

		@Test
		void onSize1() throws Exception {
			SerializedMap value = new SerializedMap(HashMap.class);
			value.useAs(Map.class);
			value.put(literal("key"), literal("value"));

			assertThat(value.toString()).isEqualTo("{key:value}");
		}
	}

	@Test
	void testAccept() throws Exception {
		SerializedMap value = new SerializedMap(HashMap.class);
		value.useAs(Map.class);

		assertThat(value.accept(new TestValueVisitor())).isEqualTo("ReferenceType:SerializedMap");
	}

}
