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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListAdaptorTest {

	private CollectionsListAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new CollectionsListAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), sameInstance(DefaultListAdaptor.class));
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class), is(false));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableList")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableRandomAccessList")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedList")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedRandomAccessList")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedList")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedRandomAccessList")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$EmptyList")), is(true));
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SingletonList")), is(true));
	}

	@Test
	public void testTryDeserializeUnmodifiable() throws Exception {
		SerializedList value = listOf("java.util.Collections$UnmodifiableList", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), listDecoratedBy("unmodifiableList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list2"));
	}

	@Test
	public void testTryDeserializeUnmodifiableRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$UnmodifiableRandomAccessList", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), listDecoratedBy("unmodifiableList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list2"));
	}

	@Test
	public void testTryDeserializeSynchronized() throws Exception {
		SerializedList value = listOf("java.util.Collections$SynchronizedList", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), listDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list2"));
	}

	@Test
	public void testTryDeserializeSynchronizedRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$SynchronizedRandomAccessList", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), listDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list2"));
	}

	@Test
	public void testTryDeserializeChecked() throws Exception {
		SerializedList value = listOf("java.util.Collections$CheckedList", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), listDecoratedBy("checkedList", Integer.class, 0, 8, 15));
		assertThat(result.getValue(), equalTo("list2"));
	}

	@Test
	public void testTryDeserializeCheckedRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$CheckedRandomAccessList", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), listDecoratedBy("checkedList", Integer.class, 0, 8, 15));
		assertThat(result.getValue(), equalTo("list2"));
	}

	@Test
	public void testTryDeserializeEmpty() throws Exception {
		SerializedList value = listOf("java.util.Collections$EmptyList");
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), containsString("List<Integer> list1 = emptyList()"));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeSingleton() throws Exception {
		SerializedList value = listOf("java.util.Collections$SingletonList", 0);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), containsString("List<Integer> list1 = singletonList(0)"));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test(expected=DeserializationException.class)
	public void testTryDeserializeOther() throws Exception {
		SerializedList value = listOf("java.lang.Object");
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		adaptor.tryDeserialize(value, generator);
	}

	private SerializedList listOf(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className)).withResult(parameterized(List.class, null, Integer.class));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> listDecoratedBy(String factory, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("ArrayList<Integer> list1 = new ArrayList<Integer>()"));
		for (int element : elements) {
			matchers.add(containsString("list1.add(" + element + ")"));
		}
		matchers.add(containsString("List<Integer> list2 = " + factory + "(list1)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> listDecoratedBy(String factory, Class<?> clazz, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("ArrayList<Integer> list1 = new ArrayList<Integer>()"));
		for (int element : elements) {
			matchers.add(containsString("list1.add(" + element + ")"));
		}
		matchers.add(containsString("List<Integer> list2 = " + factory + "(list1, " + clazz.getSimpleName() +".class)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

}
