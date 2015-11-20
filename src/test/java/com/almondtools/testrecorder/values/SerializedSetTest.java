package com.almondtools.testrecorder.values;

import static com.almondtools.testrecorder.values.GenericTypes.hashSetOfString;
import static com.almondtools.testrecorder.values.GenericTypes.setOfBounded;
import static com.almondtools.testrecorder.values.GenericTypes.setOfListOfString;
import static com.almondtools.testrecorder.values.GenericTypes.setOfString;
import static com.almondtools.testrecorder.values.ParameterizedTypeMatcher.parameterized;
import static com.almondtools.testrecorder.values.SerializedLiteral.literal;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.visitors.TestValueVisitor;

public class SerializedSetTest {

	@Test
	public void testGetTypeRaw() throws Exception {
		assertThat(new SerializedSet(Set.class).getType(), equalTo(Set.class));
	}

	@Test
	public void testGetTypeParameterized() throws Exception {
		assertThat(new SerializedSet(setOfString()).getType(), parameterized(Set.class, String.class));
	}

	@Test
	public void testGetTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfString()).getType(), parameterized(HashSet.class, String.class));
	}

	@Test
	public void testGetTypeBounded() throws Exception {
		assertThat(new SerializedSet(setOfBounded()).getType(), instanceOf(TypeVariable.class));
	}

	@Test
	public void testGetComponentTypeRaw() throws Exception {
		assertThat(new SerializedSet(Set.class).getComponentType(), equalTo(Object.class));
	}

	@Test
	public void testGetComponentTypeParameterized() throws Exception {
		assertThat(new SerializedSet(setOfString()).getComponentType(), equalTo(String.class));
	}

	@Test
	public void testGetComponentTypeNestedParameterized() throws Exception {
		assertThat(new SerializedSet(setOfListOfString()).getComponentType(), parameterized(List.class, String.class));
	}

	@Test
	public void testGetComponentTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfString()).getComponentType(), equalTo(String.class));
	}

	@Test
	public void testGetComponentTypeBounded() throws Exception {
		assertThat(new SerializedSet(setOfBounded()).getComponentType(), equalTo(Object.class));
	}

	@Test
	public void testSize0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.size(), equalTo(0));
	}

	@Test
	public void testSize1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.size(), equalTo(1));
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.isEmpty(), is(true));
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.isEmpty(), is(false));
	}

	@Test
	public void testContains0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.contains(literal(String.class, "string")), is(false));
	}

	@Test
	public void testContains1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.contains(literal(String.class, "string")), is(true));
	}

	@Test
	public void testIterator0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.iterator().hasNext(), is(false));
	}

	@Test
	public void testIterator1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.iterator().next(), equalTo(literal(String.class, "string")));
	}

	@Test
	public void testToArray0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.toArray(), emptyArray());
		assertThat(set.toArray(new SerializedValue[0]), emptyArray());
	}

	@Test
	public void testToArray1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.toArray(), arrayContaining(literal(String.class, "string")));
		assertThat(set.toArray(new SerializedValue[0]), arrayContaining(literal(String.class, "string")));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.remove(literal(String.class, "string")), is(false));
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.remove(literal(String.class, "string")), is(true));
	}

	@Test
	public void testContainsAll0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.containsAll(asList(literal(String.class, "string"))), is(false));
		assertThat(set.containsAll(asList(literal(String.class, "string"), literal(String.class, "other"))), is(false));
	}

	@Test
	public void testContainsAll1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.containsAll(asList(literal(String.class, "string"))), is(true));
		assertThat(set.containsAll(asList(literal(String.class, "string"), literal(String.class, "other"))), is(false));
	}

	@Test
	public void testAddAll() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);

		set.addAll(asList(literal(String.class, "string"), literal(String.class, "other")));

		assertThat(set, contains(literal(String.class, "string"), literal(String.class, "other")));
	}

	@Test
	public void testRemoveAll() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.addAll(asList(
			literal(String.class, "first"),
			literal(String.class, "middle"),
			literal(String.class, "other"),
			literal(String.class, "last")));
		set.removeAll(asList(literal(String.class, "middle"), literal(String.class, "other")));

		assertThat(set, contains(
			literal(String.class, "first"),
			literal(String.class, "last")));
	}

	@Test
	public void testRetainAll() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.addAll(asList(
			literal(String.class, "first"),
			literal(String.class, "middle"),
			literal(String.class, "other"),
			literal(String.class, "last")));
		set.retainAll(asList(literal(String.class, "middle"), literal(String.class, "other")));

		assertThat(set, contains(
			literal(String.class, "middle"),
			literal(String.class, "other")));
	}

	@Test
	public void testClear() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.addAll(asList(
			literal(String.class, "first"),
			literal(String.class, "middle"),
			literal(String.class, "other"),
			literal(String.class, "last")));
		set.clear();
		
		assertThat(set, empty());
	}

	@Test
	public void testToString0() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.toString(), equalTo("{}"));
	}

	@Test
	public void testToString1() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		set.add(literal(String.class, "string"));
		assertThat(set.toString(), equalTo("{string}"));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedSet set = new SerializedSet(Set.class);
		assertThat(set.accept(new TestValueVisitor()), equalTo("unknown"));
		assertThat(set.accept(new TestCollectionVisitor()), equalTo("set"));
	}

}
