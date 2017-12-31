package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenQueue;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.LocalVariable;
import net.amygdalum.testrecorder.deserializers.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicQueue;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultQueueAdaptorTest {

	private DefaultQueueAdaptor adaptor;

	@BeforeEach
	public void before() throws Exception {
		adaptor = new DefaultQueueAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent()).isNull();
	}

	@Test
	public void testMatchesLists() throws Exception {
		assertThat(adaptor.matches(Object.class)).isFalse();
		assertThat(adaptor.matches(LinkedList.class)).isTrue();
		assertThat(adaptor.matches(Queue.class)).isTrue();
		assertThat(adaptor.matches(Deque.class)).isTrue();
		assertThat(adaptor.matches(new LinkedList<Object>() {
		}.getClass())).isTrue();
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedList value = new SerializedList(parameterized(LinkedList.class, null, Integer.class)).withResult(parameterized(List.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedList<Integer> temp1 = new LinkedList<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"List<Integer> queue1 = temp1;");
		assertThat(result.getValue()).isEqualTo("queue1");
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedList value = new SerializedList(parameterized(LinkedList.class, null, Integer.class)).withResult(parameterized(LinkedList.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedList<Integer> queue1 = new LinkedList<Integer>()",
			"queue1.add(0)",
			"queue1.add(8)",
			"queue1.add(15)");
		assertThat(result.getValue()).isEqualTo("queue1");
	}

	@Test
	public void testTryDeserializeNonListResult() throws Exception {
		SerializedList value = new SerializedList(parameterized(PublicQueue.class, null, Integer.class))
			.withResult(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(Object.class);

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"PublicQueue<Integer> temp1 = new PublicQueue<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface queue1 = temp1;");
		assertThat(result.getValue()).isEqualTo("queue1");
	}

	@Test
	public void testTryDeserializeNeedingAdaptation() throws Exception {
		SerializedList value = new SerializedList(parameterized(classOfHiddenQueue(), null, Integer.class))
			.withResult(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(Object.class);

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).containsSequence(
			"java.util.Queue temp1 = (java.util.Queue<?>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenQueue\").value();",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface queue1 = (OrthogonalInterface) temp1;");
		assertThat(result.getValue()).isEqualTo("queue1");
	}

	@Test
	public void testTryDeserializeHiddenType() throws Exception {
		SerializedList value = new SerializedList(parameterized(classOfHiddenQueue(), null, Integer.class)).withResult(parameterized(LinkedList.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(Object.class);

		Computation result = adaptor.tryDeserialize(value, generator, NULL);

		assertThat(result.getStatements().toString()).doesNotContain("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenQueue");
		assertThat(result.getStatements().toString()).containsSequence(
			"LinkedList<Integer> queue1 = (LinkedList<Integer>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenQueue\").value();",
			"queue1.add(0)",
			"queue1.add(8)",
			"queue1.add(15)");
		assertThat(result.getValue()).isEqualTo("queue1");
	}

	@Test
	public void testTryDeserializeForwarded() throws Exception {
		SerializedList value = new SerializedList(parameterized(LinkedList.class, null, Integer.class)).withResult(parameterized(List.class, null, Integer.class));
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
			"LinkedList<Integer> temp1 = new LinkedList<Integer>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"forwarded.addAll(temp1);");
		assertThat(result.getValue()).isEqualTo("forwarded");
	}

}
