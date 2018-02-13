package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
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

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.deserializers.LocalVariable;
import net.amygdalum.testrecorder.deserializers.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicSet;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptorTest {

	private DefaultSetAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
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
	public void testTryDeserialize() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(Set.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashSet<Integer> temp1 = new LinkedHashSet<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"Set<Integer> set1 = temp1;");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(LinkedHashSet.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashSet<Integer> set1 = new LinkedHashSet<Integer>()",
			"set1.add(0)",
			"set1.add(8)",
			"set1.add(15)");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeNonListResult() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(PublicSet.class, null, Integer.class))
			.withResult(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"PublicSet<Integer> temp1 = new PublicSet<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface set1 = temp1;");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeNeedingAdaptation() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(classOfHiddenSet(), null, Integer.class))
			.withResult(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSequence(
			"java.util.Set temp1 = (java.util.Set<?>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenSet\").value();",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface set1 = (OrthogonalInterface) temp1;");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeNeedingHiddenAdaptation() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(classOfHiddenSet(), null, Integer.class)).withResult(parameterized(LinkedHashSet.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).doesNotContain("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenSet");
		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashSet<Integer> set1 = (LinkedHashSet<Integer>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenSet\").value();",
			"set1.add(0)",
			"set1.add(8)",
			"set1.add(15)");
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeForwarded() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(Set.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators();

		Computation result = adaptor.tryDeserialize(value, generator, new DefaultDeserializerContext() {
			@Override
			public Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation) {
				LocalVariable local = new LocalVariable("forwarded");
				local.define(type);
				return computation.define(local);
			}
		});

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedHashSet<Integer> temp1 = new LinkedHashSet<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"forwarded.addAll(temp1);");
		assertThat(result.getValue()).isEqualTo("forwarded");
	}

}
