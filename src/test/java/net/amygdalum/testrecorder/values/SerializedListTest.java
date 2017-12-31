package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.GenericTypes.arrayListOfSetOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.arrayListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.listOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.listOfString;
import static net.amygdalum.testrecorder.values.ParameterizedTypeMatcher.parameterizedType;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;
import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedListTest {

	@Test
	public void testGetResultTypeRaw() throws Exception {
		assertThat(new SerializedList(ArrayList.class).withResult(List.class).getResultType()).isEqualTo(List.class);
	}

	@Test
	public void testGetResultTypeParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfString()).withResult(listOfString()).getResultType(), parameterizedType(List.class, String.class));
	}

	@Test
	public void testGetResultTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfString()).withResult(arrayListOfString()).getResultType(), parameterizedType(ArrayList.class, String.class));
	}

	@Test
	public void testGetResultTypeBounded() throws Exception {
		assertThat(new SerializedList(ArrayList.class).withResult(listOfBounded()).getResultType(), instanceOf(TypeVariable.class));
	}

	@Test
	public void testGetComponentTypeRaw() throws Exception {
		assertThat(new SerializedList(ArrayList.class).withResult(listOfBounded()).getComponentType()).isEqualTo(Object.class);
	}

	@Test
	public void testGetComponentTypeParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfString()).withResult(listOfString()).getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testGetComponentTypeNestedParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfSetOfString()).getComponentType(), parameterizedType(Set.class, String.class));
	}

	@Test
	public void testGetComponentTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfString()).getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testWithSerializedValueArray() throws Exception {
		SerializedList result = new SerializedList(ArrayList.class)
			.withResult(arrayListOfString())
			.with(literal("a"), literal("b"));

		assertThat(result, contains(literal("a"), literal("b")));
	}

	@Test
	public void testGetComponentTypeBounded() throws Exception {
		assertThat(new SerializedList(ArrayList.class).withResult(listOfBounded()).getComponentType()).isEqualTo(Object.class);
	}

	@Test
	public void testSize0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);

		assertThat(list.size()).isEqualTo(0);
		assertThat(list.referencedValues(), empty());
	}

	@Test
	public void testSize1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));

		assertThat(list.size()).isEqualTo(1);
		assertThat(list.referencedValues(), hasSize(1));
	}

	@Test
	public void testSize2() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		list.add(literal("second"));

		assertThat(list.size()).isEqualTo(2);
		assertThat(list.referencedValues(), hasSize(2));
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.isEmpty(), is(true));
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.isEmpty(), is(false));
	}

	@Test
	public void testContains0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.contains(literal("string")), is(false));
	}

	@Test
	public void testContains1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.contains(literal("string")), is(true));
	}

	@Test
	public void testIterator0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.iterator().hasNext(), is(false));
	}

	@Test
	public void testIterator1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.iterator().next()).isEqualTo(literal("string"));
	}

	@Test
	public void testToArray0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.toArray(), emptyArray());
		assertThat(list.toArray(new SerializedValue[0]), emptyArray());
	}

	@Test
	public void testToArray1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.toArray(), arrayContaining(literal("string")));
		assertThat(list.toArray(new SerializedValue[0]), arrayContaining(literal("string")));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.remove(literal("string")), is(false));
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.remove(literal("string")), is(true));
	}

	@Test
	public void testRemoveInt0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThatThrownBy(() -> list.remove(0)).isInstanceOf(IndexOutOfBoundsException.class);
	}

	@Test
	public void testRemoveInt1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.remove(0)).isEqualTo(literal("string"));
	}

	@Test
	public void testContainsAll0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.containsAll(asList(literal("string"))), is(false));
		assertThat(list.containsAll(asList(literal("string"), literal("other"))), is(false));
	}

	@Test
	public void testContainsAll1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.containsAll(asList(literal("string"))), is(true));
		assertThat(list.containsAll(asList(literal("string"), literal("other"))), is(false));
	}

	@Test
	public void testAddAll() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);

		list.addAll(asList(literal("string"), literal("other")));

		assertThat(list, contains(literal("string"), literal("other")));
	}

	@Test
	public void testAddAllAtPos() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);

		list.addAll(asList(literal("first"), literal("last")));
		list.addAll(1, asList(literal("middle"), literal("other")));

		assertThat(list, contains(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
	}

	@Test
	public void testRemoveAll() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		list.removeAll(asList(literal("middle"), literal("other")));

		assertThat(list, contains(
			literal("first"),
			literal("last")));
	}

	@Test
	public void testRetainAll() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		list.retainAll(asList(literal("middle"), literal("other")));

		assertThat(list, contains(
			literal("middle"),
			literal("other")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		list.clear();

		assertThat(list, empty());
	}

	@Test
	public void testGet() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		assertThat(list.get(0)).isEqualTo(literal("first"));
		assertThat(list.get(2)).isEqualTo(literal("other"));
	}

	@Test
	public void testSet() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		list.set(2, literal("changed"));

		assertThat(list.get(0)).isEqualTo(literal("first"));
		assertThat(list.get(2)).isEqualTo(literal("changed"));
	}

	@Test
	public void testAdd() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		list.add(1, literal("changed"));

		assertThat(list.get(0)).isEqualTo(literal("first"));
		assertThat(list.get(1)).isEqualTo(literal("changed"));
		assertThat(list.get(2)).isEqualTo(literal("middle"));
	}

	@Test
	public void testIndexOf() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("middle"),
			literal("last")));

		assertThat(list.indexOf(literal("middle"))).isEqualTo(1);
	}

	@Test
	public void testLastIndexOf() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("middle"),
			literal("last")));

		assertThat(list.lastIndexOf(literal("middle"))).isEqualTo(2);
	}

	@Test
	public void testListIterator0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.listIterator().hasNext(), is(false));
	}

	@Test
	public void testListIterator1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.listIterator().next()).isEqualTo(literal("string"));
		assertThat(list.listIterator(1).previous()).isEqualTo(literal("string"));
	}

	@Test
	public void testSubList() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));

		assertThat(list.subList(0, 2)).isEqualTo(asList(literal("first"), literal("middle")));
		assertThat(list.subList(1, 3)).isEqualTo(asList(literal("middle"), literal("other")));
		assertThat(list.subList(2, 4)).isEqualTo(asList(literal("other"), literal("last")));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.toString()).isEqualTo("[]");
	}

	@Test
	public void testToString1() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		list.add(literal("string"));
		assertThat(list.toString()).isEqualTo("[string]");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedList list = new SerializedList(ArrayList.class).withResult(List.class);
		assertThat(list.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedList");
	}

}
