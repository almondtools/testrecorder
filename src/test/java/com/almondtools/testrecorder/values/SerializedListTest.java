package com.almondtools.testrecorder.values;

import static com.almondtools.testrecorder.values.GenericTypes.arrayListOfString;
import static com.almondtools.testrecorder.values.GenericTypes.listOfBounded;
import static com.almondtools.testrecorder.values.GenericTypes.listOfString;
import static com.almondtools.testrecorder.values.ParameterizedTypeMatcher.parameterized;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.almondtools.testrecorder.SerializedValue;

public class SerializedListTest {

	@Test
	public void testGetTypeRaw() throws Exception {
		assertThat(new SerializedList(List.class).getType(), equalTo(List.class));
	}

	@Test
	public void testGetTypeParameterized() throws Exception {
		assertThat(new SerializedList(listOfString()).getType(), parameterized(List.class, String.class));
	}

	@Test
	public void testGetTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfString()).getType(), parameterized(ArrayList.class, String.class));
	}

	@Test
	public void testGetTypeBounded() throws Exception {
		assertThat(new SerializedList(listOfBounded()).getType(), instanceOf(TypeVariable.class));
	}

	@Test
	public void testGetComponentTypeRaw() throws Exception {
		assertThat(new SerializedList(List.class).getComponentType(), equalTo(Object.class));
	}

	@Test
	public void testGetComponentTypeParameterized() throws Exception {
		assertThat(new SerializedList(listOfString()).getComponentType(), equalTo(String.class));
	}

	@Test
	public void testGetComponentTypeNestedParameterized() throws Exception {
		assertThat(new SerializedList(GenericTypes.listOfSetOfString()).getComponentType(), parameterized(Set.class, String.class));
	}

	@Test
	public void testGetComponentTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedList(arrayListOfString()).getComponentType(), equalTo(String.class));
	}

	@Test
	public void testGetComponentTypeBounded() throws Exception {
		assertThat(new SerializedList(listOfBounded()).getComponentType(), equalTo(Object.class));
	}

	@Test
	public void testSize0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.size(), equalTo(0));
	}

	@Test
	public void testSize1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.size(), equalTo(1));
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.isEmpty(), is(true));
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.isEmpty(), is(false));
	}

	@Test
	public void testContains0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.contains(new SerializedLiteral(String.class, "string")), is(false));
	}

	@Test
	public void testContains1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.contains(new SerializedLiteral(String.class, "string")), is(true));
	}

	@Test
	public void testIterator0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.iterator().hasNext(), is(false));
	}

	@Test
	public void testIterator1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.iterator().next(), equalTo(new SerializedLiteral(String.class, "string")));
	}

	@Test
	public void testToArray0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.toArray(), emptyArray());
		assertThat(list.toArray(new SerializedValue[0]), emptyArray());
	}

	@Test
	public void testToArray1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.toArray(), arrayContaining(new SerializedLiteral(String.class, "string")));
		assertThat(list.toArray(new SerializedValue[0]), arrayContaining(new SerializedLiteral(String.class, "string")));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.remove(new SerializedLiteral(String.class, "string")), is(false));
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.remove(new SerializedLiteral(String.class, "string")), is(true));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testRemoveInt0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.remove(0);
	}

	@Test
	public void testRemoveInt1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.remove(0), equalTo(new SerializedLiteral(String.class, "string")));
	}

	@Test
	public void testContainsAll0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.containsAll(asList(new SerializedLiteral(String.class, "string"))), is(false));
		assertThat(list.containsAll(asList(new SerializedLiteral(String.class, "string"), new SerializedLiteral(String.class, "other"))), is(false));
	}

	@Test
	public void testContainsAll1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.containsAll(asList(new SerializedLiteral(String.class, "string"))), is(true));
		assertThat(list.containsAll(asList(new SerializedLiteral(String.class, "string"), new SerializedLiteral(String.class, "other"))), is(false));
	}

	@Test
	public void testAddAll() throws Exception {
		SerializedList list = new SerializedList(List.class);

		list.addAll(asList(new SerializedLiteral(String.class, "string"), new SerializedLiteral(String.class, "other")));

		assertThat(list, contains(new SerializedLiteral(String.class, "string"), new SerializedLiteral(String.class, "other")));
	}

	@Test
	public void testAddAllAtPos() throws Exception {
		SerializedList list = new SerializedList(List.class);

		list.addAll(asList(new SerializedLiteral(String.class, "first"), new SerializedLiteral(String.class, "last")));
		list.addAll(1, asList(new SerializedLiteral(String.class, "middle"), new SerializedLiteral(String.class, "other")));

		assertThat(list, contains(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));
	}

	@Test
	public void testRemoveAll() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));
		list.removeAll(asList(new SerializedLiteral(String.class, "middle"), new SerializedLiteral(String.class, "other")));

		assertThat(list, contains(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "last")));
	}

	@Test
	public void testRetainAll() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));
		list.retainAll(asList(new SerializedLiteral(String.class, "middle"), new SerializedLiteral(String.class, "other")));

		assertThat(list, contains(
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));
		list.clear();
		
		assertThat(list, empty());
	}

	@Test
	public void testGet() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		assertThat(list.get(0), equalTo(new SerializedLiteral(String.class, "first")));
		assertThat(list.get(2), equalTo(new SerializedLiteral(String.class, "other")));
	}

	@Test
	public void testSet() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		list.set(2, new SerializedLiteral(String.class, "changed"));

		assertThat(list.get(0), equalTo(new SerializedLiteral(String.class, "first")));
		assertThat(list.get(2), equalTo(new SerializedLiteral(String.class, "changed")));
	}

	@Test
	public void testAdd() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		list.add(1, new SerializedLiteral(String.class, "changed"));

		assertThat(list.get(0), equalTo(new SerializedLiteral(String.class, "first")));
		assertThat(list.get(1), equalTo(new SerializedLiteral(String.class, "changed")));
		assertThat(list.get(2), equalTo(new SerializedLiteral(String.class, "middle")));
	}

	@Test
	public void testEquals() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		SerializedList expected = new SerializedList(List.class);
		expected.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		assertThat(list, equalTo(expected));
	}

	@Test
	public void testHashCode() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		SerializedList expected = new SerializedList(List.class);
		expected.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		assertThat(list.hashCode(), equalTo(expected.hashCode()));
	}

	@Test
	public void testIndexOf() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "last")));

		assertThat(list.indexOf(new SerializedLiteral(String.class, "middle")), equalTo(1));
	}

	@Test
	public void testLastIndexOf() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "last")));

		assertThat(list.lastIndexOf(new SerializedLiteral(String.class, "middle")), equalTo(2));
	}

	@Test
	public void testListIterator0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.listIterator().hasNext(), is(false));
	}

	@Test
	public void testListIterator1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.listIterator().next(), equalTo(new SerializedLiteral(String.class, "string")));
		assertThat(list.listIterator(1).previous(), equalTo(new SerializedLiteral(String.class, "string")));
	}

	@Test
	public void testSubList() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.addAll(asList(
			new SerializedLiteral(String.class, "first"),
			new SerializedLiteral(String.class, "middle"),
			new SerializedLiteral(String.class, "other"),
			new SerializedLiteral(String.class, "last")));

		assertThat(list.subList(0, 2), equalTo(asList(new SerializedLiteral(String.class, "first"), new SerializedLiteral(String.class, "middle"))));
		assertThat(list.subList(1, 3), equalTo(asList(new SerializedLiteral(String.class, "middle"), new SerializedLiteral(String.class, "other"))));
		assertThat(list.subList(2, 4), equalTo(asList(new SerializedLiteral(String.class, "other"), new SerializedLiteral(String.class, "last"))));
	}

	@Test
	public void testToString0() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.toString(), equalTo("[]"));
	}

	@Test
	public void testToString1() throws Exception {
		SerializedList list = new SerializedList(List.class);
		list.add(new SerializedLiteral(String.class, "string"));
		assertThat(list.toString(), equalTo("[string]"));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedList list = new SerializedList(List.class);
		assertThat(list.accept(new TestValueVisitor()), equalTo("unknown"));
		assertThat(list.accept(new TestCollectionVisitor()), equalTo("list"));
	}

}
