package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.GenericTypes.hashSetOfListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.hashSetOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfString;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedSetTest {

	@Test
	public void testGetResultTypeRaw() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.getUsedTypes()).containsExactly(Set.class);
	}

	@Test
	public void testGetResultTypeParameterized() throws Exception {
		SerializedSet value = new SerializedSet(hashSetOfString());
		value.useAs(setOfString());
		assertThat(value.getUsedTypes())
			.usingElementComparator(comparing(Type::getTypeName))
			.containsExactly(parameterized(Set.class, null, String.class));
	}

	@Test
	public void testGetResultTypeIndirectParameterized() throws Exception {
		SerializedSet value = new SerializedSet(hashSetOfString());
		value.useAs(hashSetOfString());
		assertThat(value.getUsedTypes())
			.usingElementComparator(comparing(Type::getTypeName))
			.containsExactly(parameterized(HashSet.class, null, String.class));
	}

	@Test
	public void testGetResultTypeBounded() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(setOfBounded());
		assertThat(value.getUsedTypes()).allMatch(type -> type instanceof TypeVariable);
	}

	@Test
	public void testGetComponentTypeRaw() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.getComponentType()).isEqualTo(Object.class);
	}

	@Test
	public void testGetComponentTypeParameterized() throws Exception {
		SerializedSet value = new SerializedSet(hashSetOfString());
		value.useAs(setOfString());
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testGetComponentTypeNestedParameterized() throws Exception {
		SerializedSet value = new SerializedSet(hashSetOfListOfString());
		value.useAs(setOfListOfString());
		assertThat(value.getComponentType()).isEqualTo(parameterized(List.class, null, String.class));
	}

	@Test
	public void testGetComponentTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfString()).getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testGetComponentTypeBounded() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(hashSetOfString());
		assertThat(value.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testWithSerializedValueArray() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class)
			.with(literal("a"), literal("b"));
		value.useAs(hashSetOfString());

		assertThat(value).containsExactly(literal("a"), literal("b"));
	}

	@Test
	public void testSize0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);

		assertThat(value.size()).isEqualTo(0);
		assertThat(value.referencedValues()).isEmpty();
	}

	@Test
	public void testSize1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));

		assertThat(value.size()).isEqualTo(1);
		assertThat(value.referencedValues()).hasSize(1);
	}

	@Test
	public void testSize2() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		value.add(literal("second"));

		assertThat(value.size()).isEqualTo(2);
		assertThat(value.referencedValues()).hasSize(2);
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.isEmpty()).isTrue();
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		assertThat(value.isEmpty()).isFalse();
	}

	@Test
	public void testContains0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.contains(literal("string"))).isFalse();
	}

	@Test
	public void testContains1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		assertThat(value.contains(literal("string"))).isTrue();
	}

	@Test
	public void testIterator0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.iterator().hasNext()).isFalse();
	}

	@Test
	public void testIterator1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		assertThat(value.iterator().next()).isEqualTo(literal("string"));
	}

	@Test
	public void testToArray0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.toArray()).isEmpty();
		assertThat(value.toArray(new SerializedValue[0])).isEmpty();
	}

	@Test
	public void testToArray1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		assertThat(value.toArray()).containsExactly(literal("string"));
		assertThat(value.toArray(new SerializedValue[0])).containsExactly(literal("string"));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.remove(literal("string"))).isFalse();
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		assertThat(value.remove(literal("string"))).isTrue();
	}

	@Test
	public void testContainsAll0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.containsAll(asList(literal("string")))).isFalse();
		assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
	}

	@Test
	public void testContainsAll1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		assertThat(value.containsAll(asList(literal("string")))).isTrue();
		assertThat(value.containsAll(asList(literal("string"), literal("other")))).isFalse();
	}

	@Test
	public void testAddAll() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);

		value.addAll(asList(literal("string"), literal("other")));

		assertThat(value).containsExactly(literal("string"), literal("other"));
	}

	@Test
	public void testRemoveAll() throws Exception {
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
	public void testRetainAll() throws Exception {
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
	public void testClear() throws Exception {
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

	@Test
	public void testToString0() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.toString()).isEqualTo("{}");
	}

	@Test
	public void testToString1() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		value.add(literal("string"));
		assertThat(value.toString()).isEqualTo("{string}");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedSet value = new SerializedSet(HashSet.class);
		value.useAs(Set.class);
		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedSet");
	}

}
