package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.GenericTypes.arrayListOfSetOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.arrayListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.listOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.listOfString;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedListTest {

	@Test
	public void testGetResultTypeRaw() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.getUsedTypes()).containsExactly(List.class);
	}

	@Test
	public void testGetResultTypeParameterized() throws Exception {
		SerializedList value = new SerializedList(arrayListOfString());
		value.useAs(listOfString());
		assertThat(value.getUsedTypes())
			.usingElementComparator(comparing(Type::getTypeName))
			.containsExactly(parameterized(List.class, null, String.class));
	}

	@Test
	public void testGetResultTypeIndirectParameterized() throws Exception {
		SerializedList value = new SerializedList(arrayListOfString());
		value.useAs(arrayListOfString());
		assertThat(value.getUsedTypes())
			.usingElementComparator(comparing(Type::getTypeName))
			.containsExactly(parameterized(ArrayList.class, null, String.class));
	}

	@Test
	public void testGetResultTypeBounded() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(listOfBounded());
		assertThat(value.getUsedTypes()).allMatch(type -> type instanceof TypeVariable);
	}

	@Test
	public void testGetComponentTypeRaw() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(listOfBounded());
		assertThat(value.getComponentType()).isEqualTo(Object.class);
	}

	@Test
	public void testGetComponentTypeParameterized() throws Exception {
		SerializedList value = new SerializedList(arrayListOfString());
		value.useAs(listOfString());
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testGetComponentTypeNestedParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfSetOfString()).getComponentType()).isEqualTo(parameterized(Set.class, null, String.class));
	}

	@Test
	public void testGetComponentTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfString()).getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testWithSerializedValueArray() throws Exception {
		SerializedList result = new SerializedList(ArrayList.class)
			.with(literal("a"), literal("b"));
		result.useAs(arrayListOfString());

		assertThat(result).containsExactly(literal("a"), literal("b"));
	}

	@Test
	public void testGetComponentTypeBounded() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(listOfBounded());
		assertThat(value.getComponentType()).isEqualTo(Object.class);
	}

	@Test
	public void testSize0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);

		assertThat(value.size()).isEqualTo(0);
		assertThat(value.referencedValues()).isEmpty();
	}

	@Test
	public void testSize1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));

		assertThat(value.size()).isEqualTo(1);
		assertThat(value.referencedValues()).hasSize(1);
	}

	@Test
	public void testSize2() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		value.add(literal("second"));

		assertThat(value.size()).isEqualTo(2);
		assertThat(value.referencedValues()).hasSize(2);
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.isEmpty()).isTrue();
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.isEmpty()).isFalse();
	}

	@Test
	public void testContains0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.contains(literal("string"))).isFalse();
	}

	@Test
	public void testContains1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.contains(literal("string"))).isTrue();
	}

	@Test
	public void testIterator0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.iterator().hasNext()).isFalse();
	}

	@Test
	public void testIterator1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.iterator().next()).isEqualTo(literal("string"));
	}

	@Test
	public void testToArray0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.toArray()).isEmpty();
		assertThat(value.toArray(new SerializedValue[0])).isEmpty();
	}

	@Test
	public void testToArray1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.toArray()).containsExactly(literal("string"));
		assertThat(value.toArray(new SerializedValue[0])).containsExactly(literal("string"));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.remove(literal("string"))).isFalse();
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.remove(literal("string"))).isTrue();
	}

	@Test
	public void testRemoveInt0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThatThrownBy(() -> value.remove(0)).isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	public void testRemoveInt1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.remove(0)).isEqualTo(literal("string"));
	}

	@Test
	public void testContainsAll0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.containsAll(asList(literal("string")))).isFalse();
		assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
	}

	@Test
	public void testContainsAll1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.containsAll(asList(literal("string")))).isTrue();
		assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
	}

	@Test
	public void testAddAll() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);

		value.addAll(asList(literal("string"), literal("other")));

		assertThat(value).containsExactly(literal("string"), literal("other"));
	}

	@Test
	public void testAddAllAtPos() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);

		value.addAll(asList(literal("first"), literal("last")));
		value.addAll(1, asList(literal("middle"), literal("other")));

		assertThat(value).containsExactly(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last"));
	}

	@Test
	public void testRemoveAll() throws Exception {
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
	public void testRetainAll() throws Exception {
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
	public void testClear() throws Exception {
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
	public void testGet() throws Exception {
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

	@Test
	public void testSet() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		value.set(2, literal("changed"));

		assertThat(value.get(0)).isEqualTo(literal("first"));
		assertThat(value.get(2)).isEqualTo(literal("changed"));
	}

	@Test
	public void testAdd() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		value.add(1, literal("changed"));

		assertThat(value.get(0)).isEqualTo(literal("first"));
		assertThat(value.get(1)).isEqualTo(literal("changed"));
		assertThat(value.get(2)).isEqualTo(literal("middle"));
	}

	@Test
	public void testIndexOf() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("middle"),
			literal("last")));

		assertThat(value.indexOf(literal("middle"))).isEqualTo(1);
	}

	@Test
	public void testLastIndexOf() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("middle"),
			literal("last")));

		assertThat(value.lastIndexOf(literal("middle"))).isEqualTo(2);
	}

	@Test
	public void testListIterator0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.listIterator().hasNext()).isFalse();
	}

	@Test
	public void testListIterator1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.listIterator().next()).isEqualTo(literal("string"));
		assertThat(value.listIterator(1).previous()).isEqualTo(literal("string"));
	}

	@Test
	public void testSubList() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		assertThat(value.subList(0, 2)).isEqualTo(asList(literal("first"), literal("middle")));
		assertThat(value.subList(1, 3)).isEqualTo(asList(literal("middle"), literal("other")));
		assertThat(value.subList(2, 4)).isEqualTo(asList(literal("other"), literal("last")));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.toString()).isEqualTo("[]");
	}

	@Test
	public void testToString1() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		value.add(literal("string"));
		assertThat(value.toString()).isEqualTo("[string]");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(List.class);
		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedList");
	}

}
