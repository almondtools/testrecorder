package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.wildcard;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenList;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
import net.amygdalum.testrecorder.util.testobjects.PublicList;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListAdaptorTest {

	private AgentConfiguration config;
	private DefaultListAdaptor adaptor;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		adaptor = new DefaultListAdaptor();
		context = new DefaultDeserializerContext();
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
	public void testTryDeserializeExplicitelyTyped() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(parameterized(List.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"List<Integer> list1 = new ArrayList<>()",
			"list1.add(0)",
			"list1.add(8)",
			"list1.add(15)");
		assertThat(result.getValue()).isEqualTo("list1");
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(parameterized(List.class, null, wildcard()));
		value.useAs(parameterized(List.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();
		
		Computation result = adaptor.tryDeserialize(value, generator, context);
		
		assertThat(result.getStatements().toString()).containsSubsequence(
			"ArrayList temp1 = new ArrayList<>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"List<Integer> list1 = temp1;");
		assertThat(result.getValue()).isEqualTo("list1");
	}
	
	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(ArrayList.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"ArrayList arrayList1 = new ArrayList<>()",
			"arrayList1.add(0)",
			"arrayList1.add(8)",
			"arrayList1.add(15)");
		assertThat(result.getValue()).isEqualTo("arrayList1");
	}

	@Test
	public void testTryDeserializeNonListResult() throws Exception {
		SerializedList value = new SerializedList(PublicList.class);
		value.useAs(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"PublicList temp1 = new PublicList<>()",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface orthogonalInterface1 = temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeNeedingAdaptation() throws Exception {
		SerializedList value = new SerializedList(classOfHiddenList());
		value.useAs(OrthogonalInterface.class);
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).containsSubsequence(
			"java.util.List temp1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenList\").value(java.util.List.class);",
			"temp1.add(0)",
			"temp1.add(8)",
			"temp1.add(15)",
			"OrthogonalInterface orthogonalInterface1 = (OrthogonalInterface) temp1;");
		assertThat(result.getValue()).isEqualTo("orthogonalInterface1");
	}

	@Test
	public void testTryDeserializeHiddenType() throws Exception {
		SerializedList value = new SerializedList(classOfHiddenList());
		value.useAs(parameterized(ArrayList.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = generator();

		Computation result = adaptor.tryDeserialize(value, generator, context);

		assertThat(result.getStatements().toString()).doesNotContain("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenList");
		assertThat(result.getStatements().toString()).containsSubsequence(
			"ArrayList<Integer> arrayList1 = clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenList\").value(ArrayList.class);",
			"arrayList1.add(0)",
			"arrayList1.add(8)",
			"arrayList1.add(15)");
		assertThat(result.getValue()).isEqualTo("arrayList1");
	}

	@Test
	public void testTryDeserializeForwarded() throws Exception {
		SerializedList value = new SerializedList(ArrayList.class);
		value.useAs(parameterized(List.class, null, Integer.class));
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
			"List<Integer> forwarded = new ArrayList<>()",
			"forwarded.add(0)",
			"forwarded.add(8)",
			"forwarded.add(15)");
		assertThat(result.getValue()).isEqualTo("forwarded");
	}

	private SetupGenerators generator() {
		return new SetupGenerators(new Adaptors<SetupGenerators>(config).load(SetupGenerator.class));
	}

}
