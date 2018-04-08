package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListAdaptorTest {

	private AgentConfiguration config;
	private CollectionsListAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new CollectionsListAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isSameAs(DefaultListAdaptor.class);
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableList"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableRandomAccessList"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedList"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedRandomAccessList"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedList"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedRandomAccessList"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$EmptyList"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SingletonList"))).isTrue();
	}

	@Test
	public void testTryDeserializeUnmodifiable() throws Exception {
		SerializedList value = listOf("java.util.Collections$UnmodifiableList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(listDecoratedBy("unmodifiableList", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeUnmodifiableRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$UnmodifiableRandomAccessList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(listDecoratedBy("unmodifiableList", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeSynchronized() throws Exception {
		SerializedList value = listOf("java.util.Collections$SynchronizedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(listDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeSynchronizedRawType() throws Exception {
		SerializedList value = listOfRaw("java.util.Collections$SynchronizedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(rawListDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeSynchronizedWildcardType() throws Exception {
		SerializedList value = listOfWildcard("java.util.Collections$SynchronizedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(wildcardListDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeSynchronizedRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$SynchronizedRandomAccessList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(listDecoratedBy("synchronizedList", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeChecked() throws Exception {
		SerializedList value = listOf("java.util.Collections$CheckedList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(listDecoratedBy("checkedList", Integer.class, 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeCheckedRandomAccess() throws Exception {
		SerializedList value = listOf("java.util.Collections$CheckedRandomAccessList", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(listDecoratedBy("checkedList", Integer.class, 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeEmpty() throws Exception {
		SerializedList value = listOf("java.util.Collections$EmptyList");
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).contains("List<Integer> list1 = emptyList()");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeSingleton() throws Exception {
		SerializedList value = listOf("java.util.Collections$SingletonList", 0);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).contains("List<Integer> list1 = singletonList(0)");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeOther() throws Exception {
		SerializedList value = listOf("java.lang.Object");
		SetupGenerators generator = new SetupGenerators(config);

		assertThrows(DeserializationException.class, () -> adaptor.tryDeserialize(value, generator, context));
	}

	private SerializedList listOf(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className));
		value.useAs(parameterized(List.class, null, Integer.class));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private SerializedList listOfRaw(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className));
		value.useAs(List.class);
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private SerializedList listOfWildcard(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className));
		value.useAs(parameterized(List.class, null, wildcard()));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private List<String> listDecoratedBy(String factory, int... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("ArrayList<Integer> arrayList1 = new ArrayList<Integer>()");
		for (int element : elements) {
			matchers.add("arrayList1.add(" + element + ")");
		}
		matchers.add("List<Integer> list1 = " + factory + "(arrayList1)");

		return matchers;
	}

	private List<String> rawListDecoratedBy(String factory, int... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("ArrayList<Object> arrayList1 = new ArrayList<Object>()");
		for (int element : elements) {
			matchers.add("arrayList1.add(" + element + ")");
		}
		matchers.add("List<Object> list1 = " + factory + "(arrayList1)");

		return matchers;
	}

	private List<String> wildcardListDecoratedBy(String factory, int... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("ArrayList arrayList1 = new ArrayList<>()");
		for (int element : elements) {
			matchers.add("arrayList1.add(" + element + ")");
		}
		matchers.add("List<?> list1 = " + factory + "(arrayList1)");

		return matchers;
	}

	private List<String> listDecoratedBy(String factory, Class<?> clazz, int... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("ArrayList<Integer> arrayList1 = new ArrayList<Integer>()");
		for (int element : elements) {
			matchers.add("arrayList1.add(" + element + ")");
		}
		matchers.add("List<Integer> list1 = " + factory + "(arrayList1, " + clazz.getSimpleName() + ".class)");

		return matchers;
	}

}
