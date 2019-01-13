package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.GenericTypes.hashSetOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfString;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedSetTest {

	@Nested
	class testGetResultType {
		@Test
		void onRaw() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);

			assertThat(value.getUsedTypes()).containsExactly(Set.class);
		}

		@Test
		void onParameterized() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(setOfString());

			assertThat(value.getUsedTypes())
				.usingElementComparator(comparing(Type::getTypeName))
				.containsExactly(parameterized(Set.class, null, String.class));
		}

		@Test
		void onIndirectParameterized() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(hashSetOfString());

			assertThat(value.getUsedTypes())
				.usingElementComparator(comparing(Type::getTypeName))
				.containsExactly(parameterized(HashSet.class, null, String.class));
		}

		@Test
		void onBounded() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(setOfBounded());

			assertThat(value.getUsedTypes()).allMatch(type -> type instanceof TypeVariable);
		}
	}

	@Nested
	class testGetComponentType {
		@Test
		void onRaw() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);

			assertThat(value.getComponentType()).isEqualTo(Object.class);
		}

		@Test
		void onParameterized() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(setOfString());

			assertThat(value.getComponentType()).isEqualTo(String.class);
		}

		@Test
		void onNestedParameterized() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(setOfListOfString());

			assertThat(value.getComponentType()).isEqualTo(parameterized(List.class, null, String.class));
		}

		@Test
		void onIndirectParameterized() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(hashSetOfString());

			assertThat(value.getComponentType()).isEqualTo(String.class);
		}

		@Test
		void onBounded() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(hashSetOfString());

			assertThat(value.getComponentType()).isEqualTo(String.class);
		}
	}

	@Test
	void testWithSerializedValueArray() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.add(literal("a"));
		value.add(literal("b"));
		value.useAs(hashSetOfString());

		assertThat(value).containsExactly(literal("a"), literal("b"));
	}

	@Nested
	class testSize {
		@Test
		void onSize0() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);

			assertThat(value.size()).isEqualTo(0);
			assertThat(value.referencedValues()).isEmpty();
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));

			assertThat(value.size()).isEqualTo(1);
			assertThat(value.referencedValues()).hasSize(1);
		}

		@Test
		void onSize2() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
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
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			assertThat(value.isEmpty()).isTrue();
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));

			assertThat(value.isEmpty()).isFalse();
		}
	}

	@Nested
	class testContains {

		@Test
		void onSize0() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);

			assertThat(value.contains(literal("string"))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));

			assertThat(value.contains(literal("string"))).isTrue();
		}
	}

	@Nested
	class testIterator {
		@Test
		void onSize0() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			assertThat(value.iterator().hasNext()).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));
			assertThat(value.iterator().next()).isEqualTo(literal("string"));
		}
	}

	@Nested
	class testToArray {

		@Test
		void onSize0() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);

			assertThat(value.toArray()).isEmpty();
			assertThat(value.toArray(new SerializedValue[0])).isEmpty();
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));

			assertThat(value.toArray()).containsExactly(literal("string"));
			assertThat(value.toArray(new SerializedValue[0])).containsExactly(literal("string"));
		}
	}

	@Nested
	class testRemove {
		@Test
		void onSize0() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			assertThat(value.remove(literal("string"))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));
			assertThat(value.remove(literal("string"))).isTrue();
		}
	}

	@Nested
	class testContainsAll {

		@Test
		void onSize0() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);

			assertThat(value.containsAll(asList(literal("string")))).isFalse();
			assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));

			assertThat(value.containsAll(asList(literal("string")))).isTrue();
			assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
		}
	}

	@Nested
	class testAdd {
		@Test
		void withAssignableType() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(parameterized(HashSet.class, null, String.class));

			value.add(literal("string"));
			value.add(nullInstance());

			assertThat(value).containsExactly(literal("string"), nullInstance());
			assertThat(value.getComponentType()).isEqualTo(String.class);
			assertThat(value.getUsedTypes()).contains(parameterized(HashSet.class, null, String.class));
		}

		@Test
		void withResettingType() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(parameterized(HashSet.class, null, String.class));

			value.add(literal(1));

			assertThat(value).containsExactly(literal(1));
			assertThat(value.getComponentType()).isEqualTo(Object.class);
			assertThat(value.getUsedTypes()).contains(parameterized(HashSet.class, null, String.class));
		}
	}

	@Nested
	class testAddAll {
		@Test
		void withAssignableTypes() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(parameterized(HashSet.class, null, String.class));

			value.addAll(asList(literal("string"), literal("other")));

			assertThat(value).containsExactly(literal("string"), literal("other"));
			assertThat(value.getComponentType()).isEqualTo(String.class);
		}

		@Test
		void withResettingType() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(parameterized(HashSet.class, null, String.class));

			value.addAll(asList(literal(1), literal("other")));

			assertThat(value).containsExactly(literal(1), literal("other"));
			assertThat(value.getComponentType()).isEqualTo(Object.class);
		}
	}

	@Test
	void testRemoveAll() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
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
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
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
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		value.clear();

		assertThat(value).isEmpty();
	}

	@Nested
	class testToString {
		@Test
		void onSize0() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);

			assertThat(value.toString()).isEqualTo("{}");
		}

		@Test
		void onSize1() throws Exception {
			SerializedSet value = new SerializedSet(HashSet.class);
			value.useAs(Set.class);
			value.add(literal("string"));

			assertThat(value.toString()).isEqualTo("{string}");
		}
	}

	@Test
	void testAccept() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);

		assertThat(value.accept(new TestValueVisitor())).isEqualTo("ReferenceType:SerializedSet");
	}

}
