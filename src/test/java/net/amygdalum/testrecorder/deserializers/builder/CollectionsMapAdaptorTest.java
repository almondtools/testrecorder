package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapAdaptorTest {

	private AgentConfiguration config;
	private CollectionsMapAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
		adaptor = new CollectionsMapAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isSameAs(DefaultMapAdaptor.class);
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableNavigableMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$UnmodifiableSortedMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedNavigableMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SynchronizedSortedMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedNavigableMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$CheckedSortedMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$EmptyMap"))).isTrue();
		assertThat(adaptor.matches(Class.forName("java.util.Collections$SingletonMap"))).isTrue();
	}

	@Test
	public void testTryDeserializeUnmodifiable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$UnmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("unmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeUnmodifiableNavigable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$UnmodifiableNavigableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("unmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeUnmodifiableSorted() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$UnmodifiableSortedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("unmodifiableMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSynchronized() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SynchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSynchronizedRawType() throws Exception {
		SerializedMap value = mapOfRaw("java.util.Collections$SynchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(rawMapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSynchronizedWildcardType() throws Exception {
		SerializedMap value = mapOfWildcard("java.util.Collections$SynchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(wildcardMapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSynchronizedNavigable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SynchronizedNavigableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSynchronizedSorted() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SynchronizedSortedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("synchronizedMap", new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeChecked() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$CheckedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("checkedMap", Integer.class, Integer.class, new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeCheckedSorted() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$CheckedSortedMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("checkedMap", Integer.class, Integer.class, new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeCheckedNavigable() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$CheckedNavigableMap", new int[] { 8, 15 }, new int[] { 47, 11 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(mapDecoratedBy("checkedMap", Integer.class, Integer.class, new int[] { 8, 15 }, new int[] { 47, 11 }));
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeEmpty() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$EmptyMap");
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).contains("Map<Integer, Integer> map1 = emptyMap()");
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSingleton() throws Exception {
		SerializedMap value = mapOf("java.util.Collections$SingletonMap", new int[] { 8, 15 });
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).contains("Map<Integer, Integer> map1 = singletonMap(8, 15)");
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeOther() throws Exception {
		SerializedMap value = mapOf("java.lang.Object");
		SetupGenerators generator = new SetupGenerators(config);

		assertThatThrownBy(() -> adaptor.tryDeserialize(value, generator, context)).isInstanceOf(DeserializationException.class);
	}

	private SerializedMap mapOf(String className, int[]... elements) throws ClassNotFoundException {
		SerializedMap value = new SerializedMap(Class.forName(className));
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		for (int[] element : elements) {
			value.put(literal(element[0]), literal(Integer.class, element[1]));
		}
		return value;
	}

	private SerializedMap mapOfRaw(String className, int[]... elements) throws ClassNotFoundException {
		SerializedMap value = new SerializedMap(Class.forName(className));
		value.useAs(Map.class);
		for (int[] element : elements) {
			value.put(literal(element[0]), literal(Integer.class, element[1]));
		}
		return value;
	}

	private SerializedMap mapOfWildcard(String className, int[]... elements) throws ClassNotFoundException {
		SerializedMap value = new SerializedMap(Class.forName(className));
		value.useAs(parameterized(Map.class, null, wildcard(), wildcard()));
		for (int[] element : elements) {
			value.put(literal(element[0]), literal(Integer.class, element[1]));
		}
		return value;
	}

	private List<String> mapDecoratedBy(String factory, int[]... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("LinkedHashMap<Integer, Integer> linkedHashMap1 = new LinkedHashMap<Integer, Integer>()");
		for (int[] element : elements) {
			matchers.add("linkedHashMap1.put(" + element[0] + ", " + element[1] + "");
		}
		matchers.add("Map<Integer, Integer> map1 = " + factory + "(linkedHashMap1)");

		return matchers;
	}

	private List<String> rawMapDecoratedBy(String factory, int[]... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("LinkedHashMap<Object, Object> linkedHashMap1 = new LinkedHashMap<Object, Object>()");
		for (int[] element : elements) {
			matchers.add("linkedHashMap1.put(" + element[0] + ", " + element[1] + "");
		}
		matchers.add("Map<Object, Object> map1 = " + factory + "(linkedHashMap1)");

		return matchers;
	}

	private List<String> wildcardMapDecoratedBy(String factory, int[]... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("LinkedHashMap linkedHashMap1 = new LinkedHashMap<>()");
		for (int[] element : elements) {
			matchers.add("linkedHashMap1.put(" + element[0] + ", " + element[1] + "");
		}
		matchers.add("Map<?, ?> map1 = " + factory + "(linkedHashMap1)");

		return matchers;
	}

	private List<String> mapDecoratedBy(String factory, Class<?> keyClazz, Class<?> valueClazz, int[]... elements) {
		List<String> matchers = new ArrayList<>();

		matchers.add("LinkedHashMap<Integer, Integer> linkedHashMap1 = new LinkedHashMap<Integer, Integer>()");
		for (int element[] : elements) {
			matchers.add("linkedHashMap1.put(" + element[0] + ", " + element[1] + "");
		}
		matchers.add("Map<Integer, Integer> map1 = " + factory + "(linkedHashMap1, " + keyClazz.getSimpleName() + ".class, " + valueClazz.getSimpleName() + ".class)");

		return matchers;
	}

}
