package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.extensions.assertj.Assertions.assertThat;
import static net.amygdalum.extensions.assertj.iterables.IterableConditions.containingExactly;
import static net.amygdalum.extensions.assertj.strings.StringConditions.containingWildcardPattern;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Collections.arrayList;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenList;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.hiddenList;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.SerializedValues;
import net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.ContainingList;
import net.amygdalum.testrecorder.util.testobjects.Cycle;
import net.amygdalum.testrecorder.util.testobjects.Dubble;
import net.amygdalum.testrecorder.util.testobjects.GenericCycle;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SetupGeneratorsTest {

	private AgentConfiguration config;
	private SerializedValues values;
	private SetupGenerators setupCode;
	private DeserializerContext context;

	@BeforeEach
	public void before() throws Exception {
		config = defaultConfig();
		values = new SerializedValues(config);
		setupCode = new SetupGenerators(config);
		context = new DefaultDeserializerContext();
	}

	@Test
	public void testVisitField() throws Exception {
		Type type = parameterized(ArrayList.class, null, String.class);
		SerializedList value = values.list(type, arrayList("Foo", "Bar"));

		Computation result = setupCode.visitField(new SerializedField(ContainingList.class, "list", type, value), context);

		assertThat(result.getStatements().toString()).containsWildcardPattern("ArrayList<String> arrayList1 = new ArrayList*");
		assertThat(result.getValue()).isEqualTo("ArrayList<String> list = arrayList1;");
	}

	@Test
	public void testVisitFieldWithCastNeeded() throws Exception {
		Computation result = setupCode.visitField(new SerializedField(Complex.class, "simple", Simple.class, values.object(Object.class, new Complex())), context);

		assertThat(result.getStatements().toString()).containsWildcardPattern("Complex complex1 = new Complex*");
		assertThat(result.getValue()).isEqualTo("Simple simple = (Simple) complex1;");
	}

	@Test
	public void testVisitFieldWithHiddenTypeAndVisibleResult() throws Exception {
		SerializedObject value = values.object(parameterized(classOfHiddenList(), null, String.class), hiddenList("Foo", "Bar"));

		Computation result = setupCode.visitField(new SerializedField(ContainingList.class, "list", parameterized(List.class, null, String.class), value), context);

		assertThat(result.getStatements().toString()).containsWildcardPattern("List list2 = new GenericObject*.as(clazz(*HiddenList*)*.value(List.class)");
		assertThat(result.getValue()).isEqualTo("List<String> list = list2;");
	}

	@Test
	public void testVisitFieldWithHiddenTypeAndHiddenResult() throws Exception {
		SerializedObject value = values.object(parameterized(classOfHiddenList(), null, String.class), hiddenList("Foo", "Bar"));

		Computation result = setupCode.visitField(new SerializedField(ContainingList.class, "list", parameterized(List.class, null, String.class), value), context);

		assertThat(result.getStatements().toString()).containsWildcardPattern("List list2 = *new GenericObject*value(List.class)");
		assertThat(result.getValue()).isEqualTo("List<String> list = list2;");
	}

	@Test
	public void testVisitReferenceType() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));

		Computation result = setupCode.visitReferenceType(value, context);

		assertThat(result.getStatements().toString()).contains("Dubble dubble1 = new Dubble(\"Foo\", \"Bar\");");
		assertThat(result.getValue()).isEqualTo("dubble1");
	}

	@Test
	public void testVisitReferenceTypeRevisited() throws Exception {
		SerializedObject value = values.object(Dubble.class, new Dubble("Foo", "Bar"));
		setupCode.visitReferenceType(value, context);

		Computation result = setupCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("dubble1");
	}

	@Test
	public void testVisitReferenceTypeForwarding() throws Exception {
		SerializedObject value = values.object(Cycle.class, Cycle.recursive("Foo"));

		Computation result = setupCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).is(containingExactly(
			containingWildcardPattern("Cycle cycle2 = GenericObject.forward(Cycle.class)*"),
			containingWildcardPattern("GenericObject.define*")));
		assertThat(result.getValue()).isEqualTo("cycle2");
	}

	@Test
	public void testVisitReferenceTypeGenericsForwarding() throws Exception {
		SerializedObject value = values.object(parameterized(GenericCycle.class, null, String.class), GenericCycle.recursive("Foo"));

		Computation result = setupCode.visitReferenceType(value, context);

		assertThat(result.getStatements()).is(containingExactly(
			containingWildcardPattern("GenericCycle genericCycle2 = GenericObject.forward(GenericCycle.class)*"),
			containingWildcardPattern("GenericObject.define*")));
		assertThat(result.getValue()).isEqualTo("genericCycle2");
	}

	@Test
	public void testVisitImmutableType() throws Exception {
		SerializedImmutable<BigInteger> value = values.bigInteger(BigInteger.valueOf(42));

		Computation result = setupCode.visitImmutableType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("new BigInteger(\"42\")");
	}

	@Test
	public void testVisitValueType() throws Exception {
		SerializedLiteral value = literal(int.class, 42);

		Computation result = setupCode.visitValueType(value, context);

		assertThat(result.getStatements()).isEmpty();
		assertThat(result.getValue()).isEqualTo("42");
	}

	@Test
	public void testTemporaryLocal() throws Exception {
		assertThat(context.temporaryLocal()).isEqualTo("temp1");
		assertThat(context.temporaryLocal()).isEqualTo("temp2");
	}

	@Test
	public void testNewLocal() throws Exception {
		assertThat(context.newLocal("var")).isEqualTo("var1");
		assertThat(context.newLocal("var")).isEqualTo("var2");
	}

}
