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
import java.util.Set;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetAdaptorTest {

	private CollectionsSetAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new CollectionsSetAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), sameInstance(DefaultSetAdaptor.class));
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class), is(false));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableNavigableSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableSortedSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedNavigableSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedSortedSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedNavigableSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedSortedSet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$EmptySet")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SingletonSet")), is(true));
	}

	@Test
	public void testTryDeserializeUnmodifiable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$UnmodifiableSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("unmodifiableSet", 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeUnmodifiableNavigable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$UnmodifiableNavigableSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), setDecoratedBy("unmodifiableSet", 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}
	
	@Test
	public void testTryDeserializeUnmodifiableSorted() throws Exception {
		SerializedSet value = setOf("java.util.Collections$UnmodifiableSortedSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("unmodifiableSet", 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeSynchronized() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SynchronizedSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeSynchronizedNavigable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SynchronizedNavigableSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeSynchronizedSorted() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SynchronizedSortedSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeChecked() throws Exception {
		SerializedSet value = setOf("java.util.Collections$CheckedSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("checkedSet", Integer.class, 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeCheckedSorted() throws Exception {
		SerializedSet value = setOf("java.util.Collections$CheckedSortedSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("checkedSet", Integer.class, 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeCheckedNavigable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$CheckedNavigableSet", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), setDecoratedBy("checkedSet", Integer.class, 0, 8, 15));
		assertThat(result.getValue(), equalTo("set2"));
	}

	@Test
	public void testTryDeserializeEmpty() throws Exception {
		SerializedSet value = setOf("java.util.Collections$EmptySet");
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), containsString("Set<Integer> set1 = emptySet()"));
		assertThat(result.getValue(), equalTo("set1"));
	}

	@Test
	public void testTryDeserializeSingleton() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SingletonSet", 0);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), containsString("Set<Integer> set1 = singleton(0)"));
		assertThat(result.getValue(), equalTo("set1"));
	}

	@Test(expected=DeserializationException.class)
	public void testTryDeserializeOther() throws Exception {
		SerializedSet value = setOf("java.lang.Object");
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		adaptor.tryDeserialize(value, generator);
	}

	private SerializedSet setOf(String className, int... elements) throws ClassNotFoundException {
		SerializedSet value = new SerializedSet(Class.forName(className)).withResult(parameterized(Set.class, null, Integer.class));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> setDecoratedBy(String factory, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("LinkedHashSet<Integer> set1 = new LinkedHashSet<Integer>()"));
		for (int element : elements) {
			matchers.add(containsString("set1.add(" + element + ")"));
		}
		matchers.add(containsString("Set<Integer> set2 = " + factory + "(set1)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> setDecoratedBy(String factory, Class<?> clazz, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("LinkedHashSet<Integer> set1 = new LinkedHashSet<Integer>()"));
		for (int element : elements) {
			matchers.add(containsString("set1.add(" + element + ")"));
		}
		matchers.add(containsString("Set<Integer> set2 = " + factory + "(set1, " + clazz.getSimpleName() +".class)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

}
