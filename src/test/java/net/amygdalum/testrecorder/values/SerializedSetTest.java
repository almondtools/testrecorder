package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.GenericTypes.hashSetOfListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.hashSetOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfBounded;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfListOfString;
import static net.amygdalum.testrecorder.values.GenericTypes.setOfString;
import static net.amygdalum.testrecorder.values.ParameterizedTypeMatcher.parameterizedType;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;
import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedSetTest {

	@Test
	public void testGetResultTypeRaw() throws Exception {
		assertThat(new SerializedSet(HashSet.class).withResult(Set.class).getResultType()).isEqualTo(Set.class);
	}

	@Test
	public void testGetResultTypeParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfString()).withResult(setOfString()).getResultType(), parameterizedType(Set.class, String.class));
	}

	@Test
	public void testGetResultTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfString()).withResult(hashSetOfString()).getResultType(), parameterizedType(HashSet.class, String.class));
	}

	@Test
	public void testGetResultTypeBounded() throws Exception {
		assertThat(new SerializedSet(HashSet.class).withResult(setOfBounded()).getResultType(), instanceOf(TypeVariable.class));
	}

	@Test
	public void testGetComponentTypeRaw() throws Exception {
		assertThat(new SerializedSet(HashSet.class).withResult(Set.class).getComponentType()).isEqualTo(Object.class);
	}

	@Test
	public void testGetComponentTypeParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfString()).withResult(setOfString()).getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testGetComponentTypeNestedParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfListOfString()).withResult(setOfListOfString()).getComponentType(), parameterizedType(List.class, String.class));
	}

	@Test
	public void testGetComponentTypeIndirectParameterized() throws Exception {
		assertThat(new SerializedSet(hashSetOfString()).getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testGetComponentTypeBounded() throws Exception {
		assertThat(new SerializedSet(HashSet.class).withResult(hashSetOfString()).getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testWithSerializedValueArray() throws Exception {
		SerializedSet result = new SerializedSet(HashSet.class)
			.withResult(hashSetOfString())
			.with(literal("a"), literal("b"));
		
		assertThat(result).containsExactly(literal("a"), literal("b"));
	}

	@Test
	public void testSize0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);

		assertThat(set.size()).isEqualTo(0);
		assertThat(set.referencedValues(), empty());
	}

	@Test
	public void testSize1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		
		assertThat(set.size()).isEqualTo(1);
		assertThat(set.referencedValues(), hasSize(1));
	}

	@Test
	public void testSize2() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		set.add(literal("second"));
		
		assertThat(set.size()).isEqualTo(2);
		assertThat(set.referencedValues(), hasSize(2));
	}

	@Test
	public void testIsEmpty0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.isEmpty()).isTrue();
	}

	@Test
	public void testIsEmpty1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		assertThat(set.isEmpty()).isFalse();
	}

	@Test
	public void testContains0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.contains(literal("string"))).isFalse();
	}

	@Test
	public void testContains1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		assertThat(set.contains(literal("string"))).isTrue();
	}

	@Test
	public void testIterator0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.iterator().hasNext()).isFalse();
	}

	@Test
	public void testIterator1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		assertThat(set.iterator().next()).isEqualTo(literal("string"));
	}

	@Test
	public void testToArray0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.toArray()).isEmpty();
		assertThat(set.toArray(new SerializedValue[0])).isEmpty();
	}

	@Test
	public void testToArray1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		assertThat(set.toArray()).containsExactly(literal("string"));
		assertThat(set.toArray(new SerializedValue[0])).containsExactly(literal("string"));
	}

	@Test
	public void testRemoveObject0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.remove(literal("string"))).isFalse();
	}

	@Test
	public void testRemoveObject1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		assertThat(set.remove(literal("string"))).isTrue();
	}

	@Test
	public void testContainsAll0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.containsAll(asList(literal("string")))).isFalse();
		assertThat(set.containsAll(asList(literal("string"), literal("other")))).isFalse();
	}

	@Test
	public void testContainsAll1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		assertThat(set.containsAll(asList(literal("string")))).isTrue();
		assertThat(set.containsAll(asList(literal("string"), literal("other")))).isFalse();
	}

	@Test
	public void testAddAll() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);

		set.addAll(asList(literal("string"), literal("other")));

		assertThat(set).containsExactly(literal("string"), literal("other"));
	}

	@Test
	public void testRemoveAll() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		set.removeAll(asList(literal("middle"), literal("other")));

		assertThat(set).containsExactly(
			literal("first"),
			literal("last"));
	}

	@Test
	public void testRetainAll() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		set.retainAll(asList(literal("middle"), literal("other")));

		assertThat(set).containsExactly(
			literal("middle"),
			literal("other"));
	}

	@Test
	public void testClear() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.addAll(asList(
			literal("first"),
			literal("middle"),
			literal("other"),
			literal("last")));
		set.clear();

		assertThat(set, empty());
	}

	@Test
	public void testToString0() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.toString()).isEqualTo("{}");
	}

	@Test
	public void testToString1() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		set.add(literal("string"));
		assertThat(set.toString()).isEqualTo("{string}");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedSet set = new SerializedSet(HashSet.class).withResult(Set.class);
		assertThat(set.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedSet");
	}

}
