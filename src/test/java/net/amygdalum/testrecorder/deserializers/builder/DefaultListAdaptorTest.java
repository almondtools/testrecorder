package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenList;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.LocalVariable;
import net.amygdalum.testrecorder.deserializers.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.Hidden;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicList;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListAdaptorTest {

	private DefaultListAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultListAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesLists() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(ArrayList.class)).isTrue();
		assertThat(adaptor.matches(LinkedList.class)).isTrue();
		assertThat(adaptor.matches(List.class)).isTrue();
		assertThat(adaptor.matches(new ArrayList<Object>() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedList value = new SerializedList(parameterized(ArrayList.class, null, Integer.class)).withResult(parameterized(List.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"ArrayList<Integer> temp1 = new ArrayList<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"List<Integer> list1 = temp1;");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedList value = new SerializedList(parameterized(ArrayList.class, null, Integer.class)).withResult(parameterized(ArrayList.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"ArrayList<Integer> list1 = new ArrayList<Integer>()",
			"list1.add(0)",
			"list1.add(8)",
			"list1.add(15)");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeNonListResult() throws Exception {
		SerializedList value = new SerializedList(parameterized(PublicList.class, null, Integer.class))
			.withResult(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(Object.class);

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"PublicList<Integer> temp1 = new PublicList<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface list1 = temp1;");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeNeedingAdaptation() throws Exception {
		SerializedList value = new SerializedList(parameterized(classOfHiddenList(), null, Integer.class))
			.withResult(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(Object.class);

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"java.util.List temp1 = (java.util.List<?>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenList\").value();",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface list1 = (OrthogonalInterface) temp1;");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeHiddenType() throws Exception {
		SerializedList value = new SerializedList(parameterized(Hidden.classOfHiddenList(), null, Integer.class)).withResult(parameterized(ArrayList.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(Object.class);

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).doesNotContain("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenList");
		assertThat(result.getStatements().toString()).containsSequence(
			"ArrayList<Integer> list1 = (ArrayList<Integer>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenList\").value();",
			"list1.add(0)",
			"list1.add(8)",
			"list1.add(15)");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserializeForwarded() throws Exception {
		SerializedList value = new SerializedList(parameterized(ArrayList.class, null, Integer.class)).withResult(parameterized(List.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass()) {
			@Override
			public Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation) {
				LocalVariable local = new LocalVariable("forwarded");
				local.define(type);
				return computation.define(local);
			}
		};

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"ArrayList<Integer> temp1 = new ArrayList<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"forwarded.addAll(temp1);");
		assertThat(result.getValue()).isEqualTo("forwarded");
	}

}
