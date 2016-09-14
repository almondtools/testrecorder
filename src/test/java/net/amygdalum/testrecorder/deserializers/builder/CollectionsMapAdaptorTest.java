package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapAdaptorTest {

	private CollectionsMapAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new CollectionsMapAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), sameInstance(DefaultMapAdaptor.class));
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class), is(false));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableNavigableMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableSortedMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedNavigableMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedSortedMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedNavigableMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedSortedMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$EmptyMap")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SingletonMap")), is(true));
	}

	@Test
	public void testTryDeserializeUnmodifiable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$UnmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("unmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeUnmodifiableNavigable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$UnmodifiableNavigableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("unmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeUnmodifiableSorted() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$UnmodifiableSortedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("unmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeSynchronized() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SynchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeSynchronizedNavigable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SynchronizedNavigableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeSynchronizedSorted() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SynchronizedSortedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeChecked() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$CheckedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("checkedMap", Integer.class, Integer.class, new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeCheckedSorted() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$CheckedSortedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("checkedMap", Integer.class, Integer.class, new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeCheckedNavigable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$CheckedNavigableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), mapDecoratedBy("checkedMap", Integer.class, Integer.class, new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeEmpty() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$EmptyMap");
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), containsString("Map<Integer, Integer> map1 = emptyMap()"));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test
	public void testTryDeserializeSingleton() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SingletonMap", new int[] { 8, 15 });
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), containsString("Map<Integer, Integer> map1 = singletonMap(8, 15)"));
		assertThat(result.getValue(), equalTo("map1"));
	}

	@Test(expected = DeserializationException.class)
	public void testTryDeserializeOther() throws Exception {
		SerializedMap value = mapOf("java.lang.Object");
		SetupGenerators generator = new SetupGenerators(getClass());

		adaptor.tryDeserialize(value, generator);
	}

	private SerializedMap mapOf(String className, int[]... elements) throws ClassNotFoundException {
		SerializedMap value = new SerializedMap(Class.forName(className)).withResult(parameterized(Map.class, null, Integer.class, Integer.class));
		for (int[] element : elements) {
			value.put(literal(element[0]), literal(Integer.class, element[1]));
		}
		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> mapDecoratedBy(String factory, int[]... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("LinkedHashMap<Integer, Integer> map2 = new LinkedHashMap<Integer, Integer>()"));
		for (int[] element : elements) {
			matchers.add(containsString("map2.put(" + element[0] + ", " + element[1] + ")"));
		}
		matchers.add(containsString("Map<Integer, Integer> map1 = " + factory + "(map2)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> mapDecoratedBy(String factory, Class<?> keyClazz, Class<?> valueClazz, int[]... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("LinkedHashMap<Integer, Integer> map2 = new LinkedHashMap<Integer, Integer>()"));
		for (int element[] : elements) {
			matchers.add(containsString("map2.put(" + element[0] + ", " + element[1] + ")"));
		}
		matchers.add(containsString("Map<Integer, Integer> map1 = " + factory + "(map2, " + keyClazz.getSimpleName() + ".class, " + valueClazz.getSimpleName() + ".class)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

}
