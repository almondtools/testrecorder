package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetAdaptorTest {

	private AgentConfiguration config;
	private CollectionsSetAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new CollectionsSetAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isSameAs(DefaultSetAdaptor.class);
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableNavigableSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableSortedSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedNavigableSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedSortedSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedNavigableSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedSortedSet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$EmptySet"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SingletonSet"))).isTrue();
	}

	@Test
	public void testTryDeserializeUnmodifiable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$UnmodifiableSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("unmodifiableSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeUnmodifiableNavigable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$UnmodifiableNavigableSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("unmodifiableSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeUnmodifiableSorted() throws Exception {
		SerializedSet value = setOf("java.util.Collections$UnmodifiableSortedSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("unmodifiableSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSynchronized() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SynchronizedSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSynchronizedRawType() throws Exception {
		SerializedSet value = setOfRaw("java.util.Collections$SynchronizedSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(rawSetDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSynchronizedWildcardType() throws Exception {
		SerializedSet value = setOfWildcard("java.util.Collections$SynchronizedSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(wildcardSetDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSynchronizedNavigable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SynchronizedNavigableSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSynchronizedSorted() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SynchronizedSortedSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("synchronizedSet", 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeChecked() throws Exception {
		SerializedSet value = setOf("java.util.Collections$CheckedSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("checkedSet", Integer.class, 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeCheckedSorted() throws Exception {
		SerializedSet value = setOf("java.util.Collections$CheckedSortedSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("checkedSet", Integer.class, 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeCheckedNavigable() throws Exception {
		SerializedSet value = setOf("java.util.Collections$CheckedNavigableSet", 0, 8, 15);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(setDecoratedBy("checkedSet", Integer.class, 0, 8, 15));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeEmpty() throws Exception {
		SerializedSet value = setOf("java.util.Collections$EmptySet");
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).contains("Set<Integer> set1 = emptySet()");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSingleton() throws Exception {
		SerializedSet value = setOf("java.util.Collections$SingletonSet", 0);
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).contains("Set<Integer> set1 = singleton(0)");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeOther() throws Exception {
		SerializedSet value = setOf("java.lang.Object");
		SetupGenerators generator = new SetupGenerators(config);

		assertThatThrownBy(() -> adaptor.tryDeserialize(value, generator, context)).isInstanceOf(DeserializationException.class);
	}

	private SerializedSet setOf(String className, int... elements) throws ClassNotFoundException {
		SerializedSet value = new SerializedSet(Class.forName(className));
		value.useAs(parameterized(Set.class, null, Integer.class));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private SerializedSet setOfRaw(String className, int... elements) throws ClassNotFoundException {
		SerializedSet value = new SerializedSet(Class.forName(className));
		value.useAs(Set.class);
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private SerializedSet setOfWildcard(String className, int... elements) throws ClassNotFoundException {
		SerializedSet value = new SerializedSet(Class.forName(className));
		value.useAs(parameterized(Set.class, null, wildcard()));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

	private List<String> setDecoratedBy(String factory, int... elements) {
		List<String> matchers = new ArrayList<>();
		matchers.add("LinkedHashSet<Integer> linkedHashSet1 = new LinkedHashSet<Integer>()");
		for (int element : elements) {
			matchers.add("linkedHashSet1.add(" + element + "");
		}
		matchers.add("Set<Integer> set1 = " + factory + "(linkedHashSet1)");

		return matchers;
	}

	private List<String> rawSetDecoratedBy(String factory, int... elements) {
		List<String> matchers = new ArrayList<>();
		matchers.add("LinkedHashSet<Object> linkedHashSet1 = new LinkedHashSet<Object>()");
		for (int element : elements) {
			matchers.add("linkedHashSet1.add(" + element + "");
		}
		matchers.add("Set<Object> set1 = " + factory + "(linkedHashSet1)");

		return matchers;
	}

	private List<String> wildcardSetDecoratedBy(String factory, int... elements) {
		List<String> matchers = new ArrayList<>();
		matchers.add("LinkedHashSet linkedHashSet1 = new LinkedHashSet<>()");
		for (int element : elements) {
			matchers.add("linkedHashSet1.add(" + element + "");
		}
		matchers.add("Set<?> set1 = " + factory + "(linkedHashSet1)");

		return matchers;
	}

	private List<String> setDecoratedBy(String factory, Class<?> clazz, int... elements) {
		List<String> matchers = new ArrayList<>();
		matchers.add("LinkedHashSet<Integer> linkedHashSet1 = new LinkedHashSet<Integer>()");
		for (int element : elements) {
			matchers.add("linkedHashSet1.add(" + element + "");
		}
		matchers.add("Set<Integer> set1 = " + factory + "(linkedHashSet1, " + clazz.getSimpleName() + ".class)");

		return matchers;
	}

}
