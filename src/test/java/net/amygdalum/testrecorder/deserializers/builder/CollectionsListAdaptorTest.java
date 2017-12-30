package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListAdaptorTest {

	private CollectionsListAdaptor adaptor;

	@BeforeEach
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
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), listDecoratedBy("unmodifiableList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeUnmodifiableRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$UnmodifiableRandomAccessList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), listDecoratedBy("unmodifiableList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeSynchronized() throws Exception {
		SerializedList value = listOf("java.util.Collections$SynchronizedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), listDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeSynchronizedRawType() throws Exception {
		SerializedList value = listOfRaw("java.util.Collections$SynchronizedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), rawListDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeSynchronizedWildcardType() throws Exception {
		SerializedList value = listOfWildcard("java.util.Collections$SynchronizedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), wildcardListDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeSynchronizedRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$SynchronizedRandomAccessList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), listDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeChecked() throws Exception {
		SerializedList value = listOf("java.util.Collections$CheckedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), listDecoratedBy("checkedList", Integer.class, 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeCheckedRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$CheckedRandomAccessList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), listDecoratedBy("checkedList", Integer.class, 0, 8, 15));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeEmpty() throws Exception {
		SerializedList value = listOf("java.util.Collections$EmptyList");
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), containsString("List<Integer> list1 = emptyList()"));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeSingleton() throws Exception {
		SerializedList value = listOf("java.util.Collections$SingletonList", 0);
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString(), containsString("List<Integer> list1 = singletonList(0)"));
		assertThat(result.getValue(), equalTo("list1"));
	}

	@Test
	public void testTryDeserializeOther() throws Exception {
		SerializedList value = listOf("java.lang.Object");
		SetupGenerators generator = new SetupGenerators(getClass());

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator, NULL));
	}

	private SerializedList listOf(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className)).withResult(parameterized(List.class, null, Integer.class));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private SerializedList listOfRaw(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className)).withResult(List.class);
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private SerializedList listOfWildcard(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className)).withResult(parameterized(List.class, null, wildcard()));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> listDecoratedBy(String factory, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("ArrayList<Integer> list2 = new ArrayList<Integer>()"));
		for (int element : elements) {
			matchers.add(containsString("list2.add(" + element + ")"));
		}
		matchers.add(containsString("List<Integer> list1 = " + factory + "(list2)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> rawListDecoratedBy(String factory, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("ArrayList<Object> list2 = new ArrayList<Object>()"));
		for (int element : elements) {
			matchers.add(containsString("list2.add(" + element + ")"));
		}
		matchers.add(containsString("List<Object> list1 = " + factory + "(list2)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> wildcardListDecoratedBy(String factory, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("ArrayList list2 = new ArrayList<>()"));
		for (int element : elements) {
			matchers.add(containsString("list2.add(" + element + ")"));
		}
		matchers.add(containsString("List<?> list1 = " + factory + "(list2)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Matcher<String> listDecoratedBy(String factory, Class<?> clazz, int... elements) {
		List<Matcher<String>> matchers = new ArrayList<>();
		matchers.add(containsString("ArrayList<Integer> list2 = new ArrayList<Integer>()"));
		for (int element : elements) {
			matchers.add(containsString("list2.add(" + element + ")"));
		}
		matchers.add(containsString("List<Integer> list1 = " + factory + "(list2, " + clazz.getSimpleName() + ".class)"));

		return Matchers.<String> allOf((Iterable<Matcher<? super String>>) (Iterable) matchers);
	}

}
