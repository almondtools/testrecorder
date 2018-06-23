package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenMap;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.awt.List;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Adaptors;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicMap;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptorTest {

	private AgentConfiguration config;
	private DefaultMapAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultMapAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(HashMap.class)).isTrue();
		assertThat(adaptor.matches(LinkedHashMap.class)).isTrue();
		assertThat(adaptor.matches(Map.class)).isTrue();
		assertThat(adaptor.matches(new LinkedHashMap<Object, Object>() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeExplicitelyTyped() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"Map<Integer, Integer> map1 = new LinkedHashMap<>()",
			"map1.put(8, 15)",
			"map1.put(47, 11)");
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(Map.class, null, wildcard(), wildcard()));
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"LinkedHashMap temp1 = new LinkedHashMap<>()",
			"temp1.put(8, 15)",
			"temp1.put(47, 11)",
			"Map<Integer, Integer> map1 = temp1;");
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(LinkedHashMap.class);
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"LinkedHashMap linkedHashMap1 = new LinkedHashMap<>()",
			"linkedHashMap1.put(8, 15)",
			"linkedHashMap1.put(47, 11)");
		assertThat(result.getValue()).isEqualTo("linkedHashMap1");
	}

	@Test
	public void testTryDeserializeNonMapResult() throws Exception {
		SerializedMap value = new SerializedMap(PublicMap.class);
		value.useAs(OrthogonalInterface.class);
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"PublicMap temp1 = new PublicMap<>()",
			"temp1.put(8, 15)",
			"temp1.put(47, 11)",
			"OrthogonalInterface orthogonalInterface1 = temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeNeedingAdaptation() throws Exception {
		SerializedMap value = new SerializedMap(classOfHiddenMap());
		value.useAs(OrthogonalInterface.class);
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"Map temp1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenMap\").value(Map.class);",
			"temp1.put(8, 15)",
			"temp1.put(47, 11)",
			"OrthogonalInterface orthogonalInterface1 = (OrthogonalInterface) temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeHiddenType() throws Exception {
		SerializedMap value = new SerializedMap(classOfHiddenMap());
		value.useAs(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).doesNotContain("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenMap");
		assertThat(result.getStatements().toString()).containsSubsequence(
			"LinkedHashMap<Integer, Integer> linkedHashMap1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenMap\").value(LinkedHashMap.class);",
			"linkedHashMap1.put(8, 15)",
			"linkedHashMap1.put(47, 11)");
		assertThat(result.getValue()).isEqualTo("linkedHashMap1");
	}

	@Test
	public void testTryDeserializeForwarded() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, new DefaultDeserializerContext() {
			@Override
			public Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation) {
				LocalVariable local = new LocalVariable("forwarded");
				local.define(type);
				return computation.define(local);
			}
		});

		assertThat(result.getStatements().toString()).containsSubsequence(
			"Map<Integer, Integer> forwarded = new LinkedHashMap<>()",
			"forwarded.put(8, 15)",
			"forwarded.put(47, 11)");
		assertThat(result.getValue()).isEqualTo("forwarded");
	}

	@Test
	public void testTryDeserializeNestedStructure() throws Exception {
		SerializedMap value = new SerializedMap(LinkedHashMap.class);
		value.useAs(parameterized(Map.class, null, Integer.class, parameterized(List.class, null, Integer.class)));
		value.put(literal(8), listOf(Integer.class, literal(15)));
		value.put(literal(47), listOf(Integer.class, literal(11), literal(11)));
		value.put(literal(11), listOf(Integer.class, literal(15), literal(47)));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"ArrayList temp1 = new ArrayList<>();",
			"temp1.add(15);",
			"List<Integer> list1 = (List<Integer>) temp1;",
			"ArrayList temp2 = new ArrayList<>();",
			"temp2.add(11);",
			"temp2.add(11);",
			"List<Integer> list2 = (List<Integer>) temp2;",
			"ArrayList temp3 = new ArrayList<>();",
			"temp3.add(15);",
			"temp3.add(47);",
			"List<Integer> list3 = (List<Integer>) temp3;",
			"Map<Integer, List<Integer>> map1 = new LinkedHashMap<>()",
			"map1.put(8, list1)",
			"map1.put(47, list2)",
			"map1.put(11, list3)");
		assertThat(result.getValue()).isEqualTo("map1");
	}

	private SerializedList listOf(Class<?> type, SerializedValue... values) {
		SerializedList list = new SerializedList(ArrayList.class);
		list.useAs(parameterized(List.class, null, type));
		list.addAll(asList(values));
		return list;
	}

	private SetupGenerators generator() {
		return new SetupGenerators(new Adaptors<SetupGenerators>(config).load(SetupGenerator.class));
	}

}
