package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenMap;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicMap;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptorTest {

	private AgentConfiguration config;
	private DefaultMapAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = new AgentConfiguration();
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
	public void testTryDeserialize() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashMap<Integer, Integer> temp1 = new LinkedHashMap<Integer, Integer>()",
			"temp1.put(8, 15)",
			"temp1.put(47, 11)",
			"Map<Integer, Integer> map1 = temp1;");
		assertThat(result.getValue()).isEqualTo("map1");
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
		value.useAs(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashMap<Integer, Integer> linkedHashMap1 = new LinkedHashMap<Integer, Integer>()",
			"linkedHashMap1.put(8, 15)",
			"linkedHashMap1.put(47, 11)");
		assertThat(result.getValue()).isEqualTo("linkedHashMap1");
	}

	@Test
	public void testTryDeserializeNonListResult() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(PublicMap.class, null, Integer.class, Integer.class));
		value.useAs(OrthogonalInterface.class);
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"PublicMap<Integer, Integer> temp1 = new PublicMap<Integer, Integer>()",
			"temp1.put(8, 15)",
			"temp1.put(47, 11)",
			"OrthogonalInterface orthogonalInterface1 = temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeNeedingAdaptation() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(classOfHiddenMap(), null, Integer.class, Integer.class));
		value.useAs(OrthogonalInterface.class);
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"Map temp1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenMap\").value(Map.class);",
			"temp1.put(8, 15)",
			"temp1.put(47, 11)",
			"OrthogonalInterface orthogonalInterface1 = (OrthogonalInterface) temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeHiddenType() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(classOfHiddenMap(), null, Integer.class, Integer.class));
		value.useAs(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).doesNotContain("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenMap");
		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashMap<Integer, Integer> linkedHashMap1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenMap\").value(LinkedHashMap.class);",
			"linkedHashMap1.put(8, 15)",
			"linkedHashMap1.put(47, 11)");
		assertThat(result.getValue()).isEqualTo("linkedHashMap1");
	}

	@Test
	public void testTryDeserializeForwarded() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
		value.useAs(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		SetupGenerators generator = new SetupGenerators(config);

		Computation result = adaptor.tryDeserialize(value, generator, new DefaultDeserializerContext() {
			@Override
			public Computation forVariable(SerializedValue value, LocalVariableDefinition computation) {
				LocalVariable local = new LocalVariable("forwarded");
				local.define(value.getType());
				return computation.define(local);
			}
		});

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashMap<Integer, Integer> temp1 = new LinkedHashMap<Integer, Integer>()",
			"temp1.put(8, 15)",
			"temp1.put(47, 11)",
			"forwarded.putAll(temp1);");
		assertThat(result.getValue()).isEqualTo("forwarded");
	}

}
