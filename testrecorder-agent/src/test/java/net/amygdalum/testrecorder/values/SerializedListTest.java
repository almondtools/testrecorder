package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.GenericTypes.arrayListOfSetOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.arrayListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.listOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.listOfString;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedListTest {

	@Nested
	class testGetUsedTypes {
		@Test
		void onRaw() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.getUsedTypes()).containsExactly(List.class);
		}

		@Test
		void onParameterized() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(arrayListOfString());

			assertThat(value.getUsedTypes())
				.usingElementComparator(comparing(Type::getTypeName))
				.containsExactly(parameterized(ArrayList.class, null, String.class));
		}

		@Test
		void onIndirectParameterized() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(arrayListOfString());

			assertThat(value.getUsedTypes())
				.usingElementComparator(comparing(Type::getTypeName))
				.containsExactly(parameterized(ArrayList.class, null, String.class));
		}

		@Test
		void onTypeBounded() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(listOfBounded());

			assertThat(value.getUsedTypes()).allMatch(type -> type instanceof TypeVariable);
		}
	}

	@Nested
	class testGetComponentType {
		@Test
		void onRaw() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(listOfBounded());

			assertThat(value.getComponentType()).isEqualTo(Object.class);
		}

		@Test
		void onParameterized() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(listOfString());

			assertThat(value.getComponentType()).isEqualTo(String.class);
		}

		@Test
		void onNestedParameterized() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(arrayListOfSetOfString());

			assertThat(value.getComponentType()).isEqualTo(parameterized(Set.class, null, String.class));
		}

		@Test
		void onIndirectParameterized() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(arrayListOfString());

			assertThat(value.getComponentType()).isEqualTo(String.class);
		}
	}

	@Test
	void testWithSerializedValueArray() throws Exception {
		SerializedList result = new SerializedList(ArrayList.class);
		result.add(literal("a"));
		result.add(literal("b"));
		result.useAs(arrayListOfString());

		assertThat(result).containsExactly(literal("a"), literal("b"));
	}

	@Test
	void testGetComponentTypeBounded() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(listOfBounded());

		assertThat(value.getComponentType()).isEqualTo(Object.class);
	}

	@Nested
	class testSize {
		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.size()).isEqualTo(0);
			assertThat(value.referencedValues()).isEmpty();
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.size()).isEqualTo(1);
			assertThat(value.referencedValues()).hasSize(1);
		}

		@Test
		void onSize2() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));
			value.add(literal("second"));

			assertThat(value.size()).isEqualTo(2);
			assertThat(value.referencedValues()).hasSize(2);
		}
	}

	@Nested
	class testIsEmpty {
		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.isEmpty()).isTrue();
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.isEmpty()).isFalse();
		}
	}

	@Nested
	class testContains {
		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.contains(literal("string"))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.contains(literal("string"))).isTrue();
		}
	}

	@Nested
	class testIterator {
		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.iterator().hasNext()).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.iterator().next()).isEqualTo(literal("string"));
		}
	}

	@Nested
	class testToArray {
		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.toArray()).isEmpty();
			assertThat(value.toArray(new SerializedValue[0])).isEmpty();
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.toArray()).containsExactly(literal("string"));
			assertThat(value.toArray(new SerializedValue[0])).containsExactly(literal("string"));
		}
	}

	@Nested
	class testRemoveObject {
		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.remove(literal("string"))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.remove(literal("string"))).isTrue();
		}
	}

	@Nested
	class testContainsAll {

		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.containsAll(asList(literal("string")))).isFalse();
			assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.containsAll(asList(literal("string")))).isTrue();
			assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
		}
	}

	@Nested
	class testAdd {
		@Test
		void withAssignableType() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(parameterized(List.class, null, String.class));

			value.add(literal("first"));

			assertThat(value).containsExactly(literal("first"));
			assertThat(value.getComponentType()).isEqualTo(String.class);
			assertThat(value.getUsedTypes()).contains(parameterized(List.class, null, String.class));
		}

		@Test
		void withResettingType() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(parameterized(List.class, null, String.class));

			value.add(literal(1));

			assertThat(value).containsExactly(literal(1));
			assertThat(value.getComponentType()).isEqualTo(Object.class);
			assertThat(value.getUsedTypes()).contains(parameterized(List.class, null, String.class));
		}
	}

	@Nested
	class testAddAll {
		@Test
		void withAssignableTypes() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(parameterized(List.class, null, String.class));

			value.addAll(asList(literal("string"), literal("other")));

			assertThat(value).containsExactly(literal("string"), literal("other"));
			assertThat(value.getComponentType()).isEqualTo(String.class);
			assertThat(value.getUsedTypes()).contains(parameterized(List.class, null, String.class));
		}

		@Test
		void withResettingType() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(parameterized(List.class, null, String.class));

			value.addAll(asList(literal(1), literal("other")));

			assertThat(value).containsExactly(literal(1), literal("other"));
			assertThat(value.getComponentType()).isEqualTo(Object.class);
			assertThat(value.getUsedTypes()).contains(parameterized(List.class, null, String.class));
		}
	}

	@Test
	void testRemoveAll() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		value.removeAll(asList(literal("middle"), literal("other")));

		assertThat(value).containsExactly(
			literal("first"),
			literal("last"));
	}

	@Test
	void testRetainAll() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		value.retainAll(asList(literal("middle"), literal("other")));

		assertThat(value).containsExactly(
			literal("middle"),
			literal("other"));
	}

	@Test
	void testClear() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		value.clear();

		assertThat(value).isEmpty();
	}

	@Test
	void testGet() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		assertThat(value.get(0)).isEqualTo(literal("first"));
		assertThat(value.get(2)).isEqualTo(literal("other"));
	}

	@Nested
	class testToString {
		@Test
		void onSize0() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);

			assertThat(value.toString()).isEqualTo("[]");
		}

		@Test
		void onSize1() throws Exception {
			SerializedList value = new SerializedList(ArrayList.class);
			value.useAs(List.class);
			value.add(literal("string"));

			assertThat(value.toString()).isEqualTo("[string]");
		}
	}

	@Test
	void testAccept() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);

		assertThat(value.accept(new TestValueVisitor())).isEqualTo("ReferenceType:SerializedList");
	}

}
