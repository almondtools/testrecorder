package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenSet;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import net.amygdalum.testrecorder.util.testobjects.PublicSet;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptorTest {

	private AgentConfiguration config;
	private DefaultSetAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultSetAdaptor();
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesSets() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(HashSet.class)).isTrue();
		assertThat(adaptor.matches(TreeSet.class)).isTrue();
		assertThat(adaptor.matches(Set.class)).isTrue();
		assertThat(adaptor.matches(SortedSet.class)).isTrue();
		assertThat(adaptor.matches(new HashSet<Object>() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserializeExplicitelyTyped() throws Exception {
		SerializedSet value = new SerializedSet(LinkedHashSet.class);
		value.useAs(parameterized(Set.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"Set<Integer> set1 = new LinkedHashSet<>()",
			"set1.add(0)",
			"set1.add(8)",
			"set1.add(15)");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedSet value = new SerializedSet(LinkedHashSet.class);
		value.useAs(parameterized(Set.class, null, wildcard()));
		value.useAs(parameterized(Set.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"LinkedHashSet temp1 = new LinkedHashSet<>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"Set<Integer> set1 = temp1;");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedSet value = new SerializedSet(LinkedHashSet.class);
		value.useAs(LinkedHashSet.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"LinkedHashSet linkedHashSet1 = new LinkedHashSet<>()",
			"linkedHashSet1.add(0)",
			"linkedHashSet1.add(8)",
			"linkedHashSet1.add(15)");
		assertThat(result.getValue()).isEqualTo("linkedHashSet1");
	}

	@Test
	public void testTryDeserializeNonSetResult() throws Exception {
		SerializedSet value = new SerializedSet(PublicSet.class);
		value.useAs(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"PublicSet temp1 = new PublicSet<>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface orthogonalInterface1 = temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeNeedingAdaptation() throws Exception {
		SerializedSet value = new SerializedSet(classOfHiddenSet());
		value.useAs(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"java.util.Set temp1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenSet\").value(java.util.Set.class);",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface orthogonalInterface1 = (OrthogonalInterface) temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeNeedingHiddenAdaptation() throws Exception {
		SerializedSet value = new SerializedSet(classOfHiddenSet());
		value.useAs(parameterized(LinkedHashSet.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).doesNotContain("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenSet");
		assertThat(result.getStatements().toString()).containsSubsequence(
			"LinkedHashSet<Integer> linkedHashSet1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenSet\").value(LinkedHashSet.class);",
			"linkedHashSet1.add(0)",
			"linkedHashSet1.add(8)",
			"linkedHashSet1.add(15)");
		assertThat(result.getValue()).isEqualTo("linkedHashSet1");
	}

	@Test
	public void testTryDeserializeForwarded() throws Exception {
		SerializedSet value = new SerializedSet(LinkedHashSet.class);
		value.useAs(parameterized(Set.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
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
			"Set<Integer> forwarded = new LinkedHashSet<>()",
			"forwarded.add(0)",
			"forwarded.add(8)",
			"forwarded.add(15)");
		assertThat(result.getValue()).isEqualTo("forwarded");
	}

	private SetupGenerators generator() {
		return new SetupGenerators(new Adaptors<SetupGenerators>(config).load(SetupGenerator.class));
	}

}
